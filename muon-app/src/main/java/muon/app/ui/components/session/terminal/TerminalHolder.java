package muon.app.ui.components.session.terminal;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import muon.app.App;
import muon.app.ui.components.ClosableTabbedPanel;
import muon.app.ui.components.session.Page;
import muon.app.ui.components.session.SessionContentPanel;
import muon.app.ui.components.session.SessionInfo;
import muon.app.ui.components.session.terminal.snippets.SnippetPanel;
import util.FontAwesomeContants;

public class TerminalHolder extends Page implements AutoCloseable {
	private ClosableTabbedPanel tabs;
//	private DefaultComboBoxModel<TerminalComponent> terminals;
//	private JComboBox<TerminalComponent> cmbTerminals;
	private JPopupMenu snippetPopupMenu;
	private SnippetPanel snippetPanel;
	private AtomicBoolean init = new AtomicBoolean(false);
	private int c = 1;
	private JButton btn;
	private SessionContentPanel sessionContentPanel;

	public TerminalHolder(SessionInfo info, SessionContentPanel sessionContentPanel) {
		this.sessionContentPanel = sessionContentPanel;
		this.tabs = new ClosableTabbedPanel(e -> {
			openNewTerminal(null);
		});

		btn = new JButton();
		btn.setToolTipText("Snippets");
		btn.addActionListener(e -> {
			showSnippets();
		});
		btn.setFont(App.SKIN.getIconFont().deriveFont(16.0f));
		btn.setText(FontAwesomeContants.FA_BOOKMARK);
		btn.putClientProperty("Nimbus.Overrides", App.SKIN.createTabButtonSkin());
		btn.setForeground(App.SKIN.getInfoTextForeground());
		tabs.getButtonsBox().add(btn);

		long t1 = System.currentTimeMillis();
		TerminalComponent tc = new TerminalComponent(info, c + "", null, sessionContentPanel);
		this.tabs.addTab(tc.getTabTitle(), tc);
		long t2 = System.currentTimeMillis();
		System.out.println("Terminal init in: " + (t2 - t1) + " ms");

//		this.terminals = new DefaultComboBoxModel<>();
//		this.cmbTerminals = new JComboBox<>(terminals);
//		this.cmbTerminals.addItemListener(e -> {
//			int index = cmbTerminals.getSelectedIndex();
//			if (index != -1) {
//				TerminalComponent tc = terminals.getElementAt(index);
//				card.show(content, tc.hashCode() + "");
//			}
//		});
//
//		Dimension dimension = new Dimension(200,
//				this.cmbTerminals.getPreferredSize().height);
//		this.cmbTerminals.setPreferredSize(dimension);
//		this.cmbTerminals.setMaximumSize(dimension);
//		this.cmbTerminals.setMinimumSize(dimension);

//		UIDefaults toolbarSkin = App.SKIN.createToolbarSkin();
//
//		this.btnStopTerm = new JButton();
//		this.btnStopTerm.setText("Close");
////        this.btnStopTerm.setFont(App.getFontAwesomeFont());
////        this.btnStopTerm.setText("\uf0c8");
//		this.btnStopTerm.setMargin(new Insets(3, 3, 3, 3));
//		this.btnStopTerm.putClientProperty("Nimbus.Overrides", toolbarSkin);
//		this.btnStopTerm.addActionListener(e -> {
//			removeTerminal();
//		});
//
//		this.btnSnippets = new JButton();
//		this.btnSnippets.setText("Snippets");
////        this.btnStopTerm.setFont(App.getFontAwesomeFont());
////        this.btnStopTerm.setText("\uf0c8");
//		this.btnSnippets.setMargin(new Insets(3, 3, 3, 3));
//		this.btnSnippets.putClientProperty("Nimbus.Overrides", toolbarSkin);
//		this.btnSnippets.addActionListener(e -> {
//			showSnippets();
//		});
//
//		this.btnStartTerm = new JButton();
//		this.btnStartTerm.setText("New terminal");
////        this.btnStartTerm.setFont(App.getFontAwesomeFont());
////        this.btnStartTerm.setText("\uf0fe");
//		this.btnStartTerm.putClientProperty("Nimbus.Overrides", toolbarSkin);
//		this.btnStartTerm.setMargin(new Insets(3, 3, 3, 3));
//		this.btnStartTerm.addActionListener(e -> {
//			createNewTerminal();
//		});
//
//		Box b1 = Box.createHorizontalBox();
////        b1.setOpaque(true);
////        b1.setBackground(new Color(250, 250, 250));
//		// b1.setBorder(new EmptyBorder(1, 2, 4, 2));
//		b1.add(Box.createRigidArea(new Dimension(10, 10)));
////        JLabel lbl=new JLabel();
////        lbl.setFont(App.getFontAwesomeFont());
////        lbl.setText("\uf120");
////        b1.add(lbl);
//		JLabel lblTerminal = new JLabel("Terminal");
//		lblTerminal.setFont(new Font(Font.DIALOG, Font.PLAIN, 14));
//		b1.add(lblTerminal);
//		b1.add(Box.createHorizontalGlue());
//		b1.add(cmbTerminals);
//		b1.add(btnStartTerm);
//		b1.add(btnStopTerm);
//		b1.add(btnSnippets);
//		this.add(b1, BorderLayout.NORTH);
//		this.add(content);
//
		snippetPanel = new SnippetPanel(e -> {
			TerminalComponent tc1 = (TerminalComponent) tabs.getSelectedContent();
			tc1.sendCommand(e + "\n");
		}, e -> {
			this.snippetPopupMenu.setVisible(false);
		});
//
		snippetPopupMenu = new JPopupMenu();
		snippetPopupMenu.add(snippetPanel);
//
//		createNewTerminal();
		this.add(tabs);
		
		addAncestorListener(new AncestorListener() {
			
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ancestorMoved(AncestorEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ancestorAdded(AncestorEvent event) {
				System.err.println("Terminal ancestor component shown");
				focusTerminal();
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				focusTerminal();
//				int index = cmbTerminals.getSelectedIndex();
//				if (index != -1) {
//					TerminalComponent comp = terminals.getElementAt(index);
//					// System.out.println("Requesting focus");
//					comp.getTerm().requestFocusInWindow();
//					// comp.setVisible(true);
//				}
//				terminals.addElement(tc);
//				cmbTerminals.setSelectedIndex(count);
				//requestFocusInWindow();
			}
		});
	}

	private void focusTerminal() {
		tabs.requestFocusInWindow();
		System.err.println("Terminal component shown");
		TerminalComponent comp = (TerminalComponent) tabs.getSelectedContent();
		if (comp != null) {
			comp.requestFocusInWindow();
			comp.getTerm().requestFocusInWindow();
		}
	}

	@Override
	public void onLoad() {
		if (init.get()) {
			return;
		}
		init.set(true);
		TerminalComponent tc = (TerminalComponent) this.tabs.getSelectedContent();
		tc.getTabTitle().getCallback().accept(tc.toString());
		tc.start();
	}

	private void showSnippets() {
		this.snippetPanel.loadSnippets();
		this.snippetPopupMenu.setLightWeightPopupEnabled(true);
		this.snippetPopupMenu.setOpaque(true);
		this.snippetPopupMenu.pack();
		this.snippetPopupMenu.setInvoker(this.btn);
		this.snippetPopupMenu.show(this.btn, this.btn.getWidth() - this.snippetPopupMenu.getPreferredSize().width,
				this.btn.getHeight());
	}

//	public void createNewTerminal(String command) {
//		// lazyInit();
//		int count = terminals.getSize();
//		TerminalComponent tc = new TerminalComponent(info, c + "", command);
//		c++;
//		content.add(tc, tc.hashCode() + "");
//		terminals.addElement(tc);
//		cmbTerminals.setSelectedIndex(count);
//		if (this.init.get()) {
//			tc.start();
//		}
//	}

//	public void createNewTerminal() {
//		createNewTerminal(null);
//	}

	public void removeTerminal() {
//		if (App.getGlobalSettings().isConfirmBeforeTerminalClosing()
//				&& JOptionPane.showConfirmDialog(null,
//						"Are you sure about closing this terminal?") != JOptionPane.YES_OPTION) {
//			return;
//		}
//		int index = cmbTerminals.getSelectedIndex();
//		if (index == -1)
//			return;
//		TerminalComponent tc = terminals.getElementAt(index);
//		terminals.removeElement(tc);
//		content.remove(tc);
//		c--;
//		threadPool.submit(() -> tc.close());
//		revalidate();
//		repaint();
	}

	public void close() {
		Component[] components = tabs.getTabContents();
		for (int i = 0; i < components.length; i++) {
			Component c = components[i];
			if (c instanceof TerminalComponent) {
				System.out.println("Closing terminal: " + c);
				((TerminalComponent) c).close();
			}
		}
		revalidate();
		repaint();
	}

	@Override
	public String getIcon() {
		return FontAwesomeContants.FA_TELEVISION;
	}

	@Override
	public String getText() {
		return "Terminal";
	}

	public void openNewTerminal(String command) {
		c++;
		TerminalComponent tc = new TerminalComponent(this.sessionContentPanel.getInfo(), c + "", command,
				this.sessionContentPanel);
		this.tabs.addTab(tc.getTabTitle(), tc);
		tc.getTabTitle().getCallback().accept(tc.toString());
		tc.start();
	}
}
