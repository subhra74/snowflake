/**
 * 
 */
package muon.app.ui.components.session.logviewer;

import java.awt.BorderLayout;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.tukaani.xz.XZInputStream;

import muon.app.App;
import muon.app.ui.components.ClosableTabContent;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextArea;
import muon.app.ui.components.TextGutter;
import muon.app.ui.components.session.SessionContentPanel;
import util.FontAwesomeContants;
import util.LayoutUtilities;

/**
 * @author subhro
 *
 */
public class LogContent extends JPanel implements ClosableTabContent {
	private SessionContentPanel holder;
	private String remoteFile;
	private File indexFile;
	private RandomAccessFile raf;
	private long totalLines;
	private int linePerPage = 50;
	private long currentPage;
	private long pageCount;
	private JButton btnNextPage, btnPrevPage, btnFirstPage, btnLastPage;
	private JTextArea textArea;
	private JLabel lblCurrentPage, lblTotalPage;
	private PagedLogSearchPanel logSearchPanel;
	private Highlighter.HighlightPainter painter;
	private TextGutter gutter;
	private StartPage startPage;
	private Consumer<String> callback;

	/**
	 * 
	 */
	public LogContent(SessionContentPanel holder, String remoteLogFile,
			StartPage startPage, Consumer<String> callback) {
		super(new BorderLayout(), true);
		this.holder = holder;
		this.callback = callback;
		this.startPage = startPage;
		this.remoteFile = remoteLogFile;
		lblCurrentPage = new JLabel();
		lblCurrentPage.setHorizontalAlignment(JLabel.CENTER);
		lblTotalPage = new JLabel();
		lblTotalPage.setHorizontalAlignment(JLabel.CENTER);

		UIDefaults skin = App.SKIN.createToolbarSkin();

		btnFirstPage = new JButton();
		btnFirstPage.setToolTipText("First page");
		btnFirstPage.putClientProperty("Nimbus.Overrides", skin);
		btnFirstPage.setFont(App.SKIN.getIconFont());
		btnFirstPage.setText(FontAwesomeContants.FA_FAST_BACKWARD);
		btnFirstPage.addActionListener(e -> {
			firstPage();
		});

		btnNextPage = new JButton();
		btnNextPage.setToolTipText("Next page");
		btnNextPage.putClientProperty("Nimbus.Overrides", skin);
		btnNextPage.setFont(App.SKIN.getIconFont());
		btnNextPage.setText(FontAwesomeContants.FA_STEP_FORWARD);
		btnNextPage.addActionListener(e -> {
			nextPage();
		});

		btnPrevPage = new JButton("");
		btnPrevPage.setToolTipText("Previous page");
		btnPrevPage.putClientProperty("Nimbus.Overrides", skin);
		btnPrevPage.setFont(App.SKIN.getIconFont());
		btnPrevPage.setText(FontAwesomeContants.FA_STEP_BACKWARD);
		btnPrevPage.addActionListener(e -> {
			previousPage();
		});

		btnLastPage = new JButton();
		btnLastPage.setToolTipText("Last page");
		btnLastPage.putClientProperty("Nimbus.Overrides", skin);
		btnLastPage.setFont(App.SKIN.getIconFont());
		btnLastPage.setText(FontAwesomeContants.FA_FAST_FORWARD);
		btnLastPage.addActionListener(e -> {
			lastPage();
		});

		textArea = new SkinnedTextArea();
		textArea.setEditable(false);
		textArea.setBackground(App.SKIN.getSelectedTabColor());
		textArea.setWrapStyleWord(true);
		textArea.setFont(textArea.getFont().deriveFont(
				(float) App.getGlobalSettings().getLogViewerFont()));
		this.textArea
				.setLineWrap(App.getGlobalSettings().isLogViewerUseWordWrap());

		gutter = new TextGutter(textArea);
		JScrollPane scrollPane = new SkinnedScrollPane(textArea);
		scrollPane.setRowHeaderView(gutter);
		this.add(scrollPane);

		JCheckBox chkLineWrap = new JCheckBox("Word wrap");

		chkLineWrap.addActionListener(e -> {
			this.textArea.setLineWrap(chkLineWrap.isSelected());
			App.getGlobalSettings()
					.setLogViewerUseWordWrap(chkLineWrap.isSelected());
			App.saveSettings();
		});

		chkLineWrap
				.setSelected(App.getGlobalSettings().isLogViewerUseWordWrap());

		SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(
				App.getGlobalSettings().getLogViewerFont(), 5, 255, 1);
		JSpinner spFontSize = new JSpinner(spinnerNumberModel);
		spFontSize.setMaximumSize(spFontSize.getPreferredSize());
		spFontSize.addChangeListener(e -> {
			int fontSize = (int) spinnerNumberModel.getValue();
			textArea.setFont(textArea.getFont().deriveFont((float) fontSize));
			gutter.setFont(textArea.getFont());
			gutter.revalidate();
			gutter.repaint();
			App.getGlobalSettings().setLogViewerFont(fontSize);
			App.saveSettings();
		});

		JButton btnReload = new JButton();
		btnReload.setToolTipText("Reload");
		btnReload.putClientProperty("Nimbus.Overrides", skin);
		btnReload.setFont(App.SKIN.getIconFont());
		btnReload.setText(FontAwesomeContants.FA_UNDO);
		btnReload.addActionListener(e -> {
			try {
				raf.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Files.delete(this.indexFile.toPath());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			this.currentPage = 0;
			initPages();
		});

		JButton btnBookMark = new JButton();
		btnBookMark.setToolTipText("Add to bookmark/pin");
		btnBookMark.putClientProperty("Nimbus.Overrides", skin);
		btnBookMark.setFont(App.SKIN.getIconFont());
		btnBookMark.setText(FontAwesomeContants.FA_BOOKMARK);
		btnBookMark.addActionListener(e -> {
			startPage.pinLog(remoteLogFile);
		});

		Box toolbar = Box.createHorizontalBox();
		toolbar.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 1, 0, App.SKIN.getDefaultBorderColor()),
				new EmptyBorder(5, 10, 5, 10)));
		toolbar.add(btnFirstPage);
		toolbar.add(btnPrevPage);
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(lblCurrentPage);
		toolbar.add(lblTotalPage);
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(btnNextPage);
		toolbar.add(btnLastPage);
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(chkLineWrap);
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(spFontSize);
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(btnReload);
		toolbar.add(Box.createHorizontalStrut(10));
		toolbar.add(btnBookMark);

		this.add(toolbar, BorderLayout.NORTH);

		logSearchPanel = new PagedLogSearchPanel(new SearchListener() {
			@Override
			public void search(String text) {
				AtomicBoolean stopFlag = new AtomicBoolean(false);
				holder.disableUi(stopFlag);
				holder.EXECUTOR.execute(() -> {
					try {
						RandomAccessFile searchIndex = LogContent.this
								.search(text, stopFlag);
						long len = searchIndex.length();
						SwingUtilities.invokeLater(() -> {
							logSearchPanel.setResults(searchIndex, len);
						});
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						holder.enableUi();
					}
				});
			}

			@Override
			public void select(long index) {
				System.out.println("Search item found on line: " + index);
				int page = (int) index / linePerPage;
				int line = (int) (index % linePerPage);
				System.out.println("Found on page: " + page + " line: " + line);
				if (currentPage == page) {
					if (line < textArea.getLineCount() && line != -1) {
						highlightLine(line);
					}
				} else {
					currentPage = page;
					loadPage(line);
				}
			}
		});
		this.add(logSearchPanel, BorderLayout.SOUTH);

		painter = new DefaultHighlighter.DefaultHighlightPainter(
				App.SKIN.getAddressBarSelectionBackground());

		initPages();
	}

	private void initPages() {
		AtomicBoolean stopFlag = new AtomicBoolean(false);
		holder.disableUi(stopFlag);
		holder.EXECUTOR.execute(() -> {
			try {
				if ((indexFile(true, stopFlag))
						|| (indexFile(false, stopFlag))) {
					this.totalLines = this.raf.length() / 16;
					System.out.println("Total lines: " + this.totalLines);
					if (this.totalLines > 0) {
						this.pageCount = (long) Math
								.ceil((double) totalLines / linePerPage);
						System.out
								.println("Number of pages: " + this.pageCount);
						if (this.currentPage > this.pageCount) {
							this.currentPage = this.pageCount;
						}
						String pageText = getPageText(this.currentPage,
								stopFlag);
						SwingUtilities.invokeLater(() -> {

							this.lblTotalPage.setText(
									String.format("/ %d ", this.pageCount));
							this.lblCurrentPage
									.setText((this.currentPage + 1) + "");

							LayoutUtilities.equalizeSize(this.lblTotalPage,
									this.lblCurrentPage);

							this.textArea.setText(pageText);
							if (pageText.length() > 0) {
								this.textArea.setCaretPosition(0);
							}

							gutter.setLineStart(1);

							this.revalidate();
							this.repaint();
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				holder.enableUi();
			}
		});
	}

	private String getPageText(long page, AtomicBoolean stopFlag)
			throws Exception {
		long lineStart = page * linePerPage;
		long lineEnd = lineStart + linePerPage - 1;

		// System.out.println("Start line: " + lineStart + "\nEnd line: " +
		// lineEnd);

		StringBuilder command = new StringBuilder();

		raf.seek(lineStart * 16);
		byte[] longBytes = new byte[8];
		if (raf.read(longBytes) != 8) {
			throw new Exception("EOF found");
		}

		long startOffset = ByteBuffer.wrap(longBytes).getLong();// Longs.fromByteArray(longBytes);

		// System.out.println("startOffset: " + startOffset);

		raf.seek(lineEnd * 16);
		if (raf.read(longBytes) != 8) {
			raf.seek(raf.length() - 16);
			raf.read(longBytes);
		}

		long endOffset = ByteBuffer.wrap(longBytes).getLong();// Longs.fromByteArray(longBytes);
		raf.seek(lineEnd * 16 + 8);
		if (raf.read(longBytes) != 8) {
			raf.seek(raf.length() - 8);
			raf.read(longBytes);
		}
		long lineLength = ByteBuffer.wrap(longBytes).getLong();// Longs.fromByteArray(longBytes);

		// System.out.println("endOffset: " + endOffset + " lineLength: " +
		// lineLength);

		endOffset = endOffset + lineLength;

		long byteRange = endOffset - startOffset;

		if (startOffset < 8192) {
			command.append("dd if=\"" + this.remoteFile + "\" ibs=1 skip="
					+ startOffset + " count=" + byteRange
					+ " 2>/dev/null | sed -ne '1," + linePerPage + "p;"
					+ (linePerPage + 1) + "q'");
		} else {
			long blockToSkip = startOffset / 8192;
			long bytesToSkip = startOffset % 8192;
			int blocks = (int) Math.ceil((double) byteRange / 8192);

//			System.out.println("Byte range: "
//					+ FormatUtils.humanReadableByteCount(byteRange, true));

			if (blocks * 8192 - bytesToSkip < byteRange) {
				blocks++;
			}
			command.append("dd if=\"" + this.remoteFile + "\" ibs=8192 skip="
					+ blockToSkip + " count=" + blocks
					+ " 2>/dev/null | dd bs=1 skip=" + bytesToSkip
					+ " 2>/dev/null | sed -ne '1," + linePerPage + "p;"
					+ (linePerPage + 1) + "q'");
		}

		// String command = "sed -ne '" + (lineStart + 1) + "," + lineEnd + "p;"
		// + (lineEnd + 1) + "q' \"" + filePath + "\"";
		System.out.println("Command: " + command);
		StringBuilder output = new StringBuilder();

		if (holder.getRemoteSessionInstance().exec(command.toString(), stopFlag,
				output) == 0) {
			return output.toString();
		}
		return null;
	}

	private boolean indexFile(boolean xz, AtomicBoolean stopFlag) {
		try {
			File tempFile = Files.createTempFile(
					"muon" + UUID.randomUUID().toString(), "index").toFile();
			System.out.println("Temp file: " + tempFile);
			try (OutputStream outputStream = new FileOutputStream(tempFile)) {
				String command = "LANG=C awk '{len=length($0); print len; }' \""
						+ remoteFile + "\" | " + (xz ? "xz" : "gzip") + " |cat";
				System.out.println("Command: " + command);

				if (holder.getRemoteSessionInstance().execBin(command, stopFlag,
						outputStream, null) == 0) {

					try (InputStream inputStream = new FileInputStream(
							tempFile);
							InputStream gzIn = xz
									? new XZInputStream(inputStream)
									: new GZIPInputStream(inputStream)) {
						this.indexFile = createIndexFile(gzIn);
						this.raf = new RandomAccessFile(this.indexFile, "r");
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private File createIndexFile(InputStream inputStream) throws Exception {
		byte[] longBytes = new byte[8];
		long offset = 0;
		File tempFile = Files
				.createTempFile("muon" + UUID.randomUUID().toString(), "index")
				.toFile();
		try (OutputStream outputStream = new FileOutputStream(tempFile);
				BufferedReader br = new BufferedReader(
						new InputStreamReader(inputStream));
				BufferedOutputStream bout = new BufferedOutputStream(
						outputStream)) {
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				line = line.trim();
				if (line.length() < 1)
					continue;
				toByteArray(offset, longBytes);
				bout.write(longBytes);
				long len = Long.parseLong(line);
				toByteArray(len, longBytes);
				bout.write(longBytes);
				offset += (len + 1);
			}
		}
		return tempFile;
	}

	private static void toByteArray(long value, byte[] result) {
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte) (value & 0xffL);
			value >>= 8;
		}
	}

	private void nextPage() {
		if (currentPage < pageCount - 1) {
			currentPage++;
			loadPage();
		}
	}

	private void previousPage() {
		if (currentPage > 0) {
			currentPage--;
			loadPage();
		}
	}

	private void firstPage() {
		currentPage = 0;
		loadPage();
	}

	private void lastPage() {
		if (this.pageCount > 0) {
			currentPage = this.pageCount - 1;
			loadPage();
		}
	}

	public void loadPage() {
		loadPage(-1);
	}

	public void loadPage(int line) {
		AtomicBoolean stopFlag = new AtomicBoolean(false);
		holder.disableUi(stopFlag);
		holder.EXECUTOR.execute(() -> {
			try {
				String pageText = getPageText(this.currentPage, stopFlag);
				SwingUtilities.invokeLater(() -> {
					this.textArea.setText(pageText);
					if (pageText.length() > 0) {
						this.textArea.setCaretPosition(0);
					}
					this.lblCurrentPage.setText((this.currentPage + 1) + "");
					LayoutUtilities.equalizeSize(this.lblTotalPage,
							this.lblCurrentPage);
					if (line < textArea.getLineCount() && line != -1) {
						highlightLine(line);
					}
					long lineStart = this.currentPage * linePerPage;
					gutter.setLineStart(lineStart + 1);
//					lineModel.clear();
//					String lines[] = pageText.replace("\t", "    ").split("\n");
//					lineModel.addAll(Arrays.asList(lines));
//					if (line < lineModel.getSize() && line != -1) {
//						lineList.setSelectedIndex(line);
//						lineList.ensureIndexIsVisible(line);
//					}
//					this.txtCurrentPage.setText((this.currentPage + 1) + "");
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				holder.enableUi();
			}
		});
	}

	@Override
	public boolean close() {
		try {
			if (raf != null) {
				raf.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			Files.delete(this.indexFile.toPath());
		} catch (Exception ex) {
		}
		callback.accept(remoteFile);
		return true;
	}

	private RandomAccessFile search(String text, AtomicBoolean stopFlag)
			throws Exception {
		byte[] longBytes = new byte[8];
		File tempFile = Files
				.createTempFile("muon" + UUID.randomUUID().toString(), "index")
				.toFile();
		// List<Integer> list = new ArrayList<>();
		StringBuilder command = new StringBuilder();
		command.append("awk '{if(index(tolower($0),\""
				+ text.toLowerCase(Locale.ENGLISH) + "\")){ print NR}}' \""
				+ this.remoteFile + "\"");
		System.out.println("Command: " + command);
		try (OutputStream outputStream = new FileOutputStream(tempFile)) {

			File searchIndexes = Files.createTempFile(
					"muon" + UUID.randomUUID().toString(), "index").toFile();
			if (holder.getRemoteSessionInstance().execBin(command.toString(),
					stopFlag, outputStream, null) == 0) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(tempFile)));
						OutputStream out = new FileOutputStream(searchIndexes);
						BufferedOutputStream bout = new BufferedOutputStream(
								out)) {
					while (true) {
						String line = br.readLine();
						if (line == null)
							break;
						line = line.trim();
						if (line.length() < 1)
							continue;
						long lineNo = Long.parseLong(line);
						toByteArray(lineNo, longBytes);
						bout.write(longBytes);
					}
					return new RandomAccessFile(searchIndexes, "r");
				}
			}
		}
		return null;
	}

	private void highlightLine(int lineNumber) {
		try {
			int startIndex = textArea.getLineStartOffset(lineNumber);
			int endIndex = textArea.getLineEndOffset(lineNumber);
			System.out.println("selection: " + startIndex + " " + endIndex);
			textArea.setCaretPosition(startIndex);
			textArea.getHighlighter().removeAllHighlights();
			textArea.getHighlighter().addHighlight(startIndex, endIndex,
					painter);
			System.out.println(textArea.modelToView2D(startIndex));
			// textArea.select(startIndex, endIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the remoteFile
	 */
	public String getRemoteFile() {
		return remoteFile;
	}
}
