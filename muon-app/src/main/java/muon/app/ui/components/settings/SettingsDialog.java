/**
 * 
 */
package muon.app.ui.components.settings;

import com.jediterm.terminal.emulator.ColorPalette;
import muon.app.App;
import muon.app.PasswordStore;
import muon.app.Settings;
import muon.app.ui.components.KeyShortcutComponent;
import muon.app.ui.components.SkinnedScrollPane;
import muon.app.ui.components.SkinnedTextField;
import muon.app.ui.components.session.files.transfer.FileTransfer.TransferMode;
import util.FontUtils;
import util.Language;
import util.LayoutUtilities;
import util.OptionPaneUtils;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static muon.app.App.bundle;

/**
 * @author subhro
 *
 */
public class SettingsDialog extends JDialog {
	private final EditorTableModel editorModel = new EditorTableModel();
	private JSpinner spTermWidth, spTermHeight, spFontSize;
	private JCheckBox chkAudibleBell, chkPuttyLikeCopyPaste;
	private JComboBox<String> cmbFonts, cmbTermType, cmbTermPalette, cmbLanguage;
	private JComboBox<TerminalTheme> cmbTermTheme;
	private final JButton btnSave;
	private final JButton btnCancel;
	private final JButton btnReset;
	private ColorSelectorButton[] paletteButtons;
	private ColorSelectorButton defaultColorFg, defaultColorBg, defaultSelectionFg, defaultSelectionBg, defaultFoundFg,
			defaultFoundBg;
	private JCheckBox chkConfirmBeforeDelete, chkConfirmBeforeMoveOrCopy, chkShowHiddenFilesByDefault, chkPromptForSudo,
			chkDirectoryCache, chkShowPathBar, chkConfirmBeforeTerminalClosing, chkShowMessagePrompt,
			chkUseGlobalDarkTheme;
	private KeyShortcutComponent[] kcc;

	private JCheckBox chkLogWrap;
	private JSpinner spLogLinesPerPage, spLogFontSize;

	private JSpinner spSysLoadInterval;
	private JComboBox<String> cmbTransferMode, cmbConflictAction;
	private final DefaultComboBoxModel<String> conflictOptions = new DefaultComboBoxModel<>();

	private final List<String> conflictOption1 = Arrays.asList(bundle.getString("overwrite"), bundle.getString("auto_rename"), bundle.getString("skip"), bundle.getString("prompt"));
	private final List<String> conflictOption2 = Arrays.asList(bundle.getString("overwrite"), bundle.getString("auto_rename"), bundle.getString("skip"));



	private JTable editorTable;

	private final CardLayout cardLayout;
	private final JPanel cardPanel;
	private final JList<String> navList;

	private JCheckBox chkUseManualScaling;
	private JSpinner spScaleValue;

	private JCheckBox chkUseMasterPassword;
	private JButton btnChangeMasterPassword;

	/**
	 * 
	 */
	public SettingsDialog(JFrame window) {
		super(window);
		setTitle(bundle.getString("settings"));
		setModal(true);
		setSize(800, 600);

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout, true);

		DefaultListModel<String> navModel = new DefaultListModel<>();
		navList = new JList<String>(navModel);
		navList.setCellRenderer(new CellRenderer());

		navList.addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int index = navList.getSelectedIndex();
				if (index != -1) {
					String value = navModel.get(index);
					cardLayout.show(cardPanel, value);
					this.revalidate();
					this.repaint(0);
				}
			}
		});

		Map<String, Component> panelMap = new LinkedHashMap<>();

		panelMap.put(bundle.getString("general"), createGeneralPanel());
		panelMap.put(bundle.getString("terminal"), createTerminalPanel());
		panelMap.put(bundle.getString("editor"), createEditorPanel());
		panelMap.put(bundle.getString("display"), createMiscPanel());
		panelMap.put(bundle.getString("security"), createSecurityPanel());

		for (String key : panelMap.keySet()) {
			navModel.addElement(key);
			cardPanel.add(panelMap.get(key), key);
		}

		JScrollPane scrollPane = new SkinnedScrollPane(navList);
		scrollPane.setPreferredSize(new Dimension(150, 200));
		scrollPane.setBorder(new MatteBorder(0, 0, 0, 1, App.SKIN.getDefaultBorderColor()));

		Box bottomBox = Box.createHorizontalBox();
		bottomBox.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, App.SKIN.getDefaultBorderColor()),
				new EmptyBorder(10, 10, 10, 10)));

		btnCancel = new JButton(bundle.getString("cancel"));
		btnSave = new JButton(bundle.getString("save"));
		btnReset = new JButton(bundle.getString("reset"));

		btnSave.addActionListener(e -> {
			applySettings();
		});

		btnCancel.addActionListener(e -> {
			super.setVisible(false);
		});

		btnReset.addActionListener(e -> {
			loadSettings(new Settings());
			JOptionPane.showMessageDialog(this, bundle.getString("settings_saved"));
		});

		bottomBox.add(btnReset);
		bottomBox.add(Box.createHorizontalGlue());
		bottomBox.add(btnCancel);
		bottomBox.add(Box.createHorizontalStrut(5));
		bottomBox.add(btnSave);

		this.add(scrollPane, BorderLayout.WEST);
		this.add(cardPanel);
		this.add(bottomBox, BorderLayout.SOUTH);

		navList.setSelectedIndex(0);
	}

	private void resizeNumericSpinner(JSpinner spinner) {
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		Number val = (Number) model.getValue();
		Number max = (Number) model.getMaximum();
		spinner.getModel().setValue(max);
		Dimension d = spinner.getPreferredSize();
		spinner.getModel().setValue(val);
		spinner.setPreferredSize(d);
		spinner.setMinimumSize(d);
		spinner.setMaximumSize(d);
	}

	private Component createRow(Component... components) {
		Box box = Box.createHorizontalBox();
		box.setAlignmentX(Box.LEFT_ALIGNMENT);
		for (Component c : components) {
			box.add(c);
		}
		return box;
	}

	private JLabel createTitleLabel(String text) {
		JLabel lblText = new JLabel(text);
		lblText.setFont(App.SKIN.getDefaultFont().deriveFont(14.0f));
		lblText.setAlignmentX(Box.LEFT_ALIGNMENT);
		return lblText;
	}

	private Component createTerminalPanel() {
		spTermWidth = new JSpinner(new SpinnerNumberModel(80, 16, 511, 1));
		spTermHeight = new JSpinner(new SpinnerNumberModel(24, 4, 511, 1));

		resizeNumericSpinner(spTermWidth);
		resizeNumericSpinner(spTermHeight);

		chkAudibleBell = new JCheckBox("Terminal bell");

		cmbFonts = new JComboBox<>(FontUtils.TERMINAL_FONTS.keySet().toArray(new String[0]));
		cmbFonts.setRenderer(new FontItemRenderer());
		Dimension d = new Dimension(cmbFonts.getPreferredSize().width * 2, cmbFonts.getPreferredSize().height);// cmbFonts.getPreferredSize();
		cmbFonts.setPreferredSize(d);
		cmbFonts.setMaximumSize(d);
		cmbFonts.setMinimumSize(d);

		spFontSize = new JSpinner(new SpinnerNumberModel(12, 1, Short.MAX_VALUE, 1));
		resizeNumericSpinner(spFontSize);

		Component boxTermSize = createRow(new JLabel(bundle.getString("columns")), Box.createRigidArea(new Dimension(10, 10)),
				spTermWidth, Box.createRigidArea(new Dimension(20, 10)), new JLabel(bundle.getString("rows")),
				Box.createRigidArea(new Dimension(10, 10)), spTermHeight, Box.createHorizontalGlue(),
				new JButton(bundle.getString("reset")));

		Component boxTermBell = createRow(chkAudibleBell);

		Component boxFontRow = createRow(new JLabel(bundle.getString("font_name")), Box.createRigidArea(new Dimension(10, 10)), cmbFonts,
				Box.createRigidArea(new Dimension(20, 10)), new JLabel(bundle.getString("font_size")),
				Box.createRigidArea(new Dimension(10, 10)), spFontSize);

		chkPuttyLikeCopyPaste = new JCheckBox(bundle.getString("copy_like_putty"));

		cmbTermType = new JComboBox<>(new String[] { "xterm-256color", "xterm", "vt100" });
		cmbTermType.setEditable(true);
		d = new Dimension(Math.max(100, cmbTermType.getPreferredSize().width * 2),
				cmbTermType.getPreferredSize().height);
		cmbTermType.setMaximumSize(d);
		cmbTermType.setMinimumSize(d);
		cmbTermType.setPreferredSize(d);

		Component boxTermType = createRow(new JLabel(bundle.getString("terminal_type")), Box.createRigidArea(new Dimension(10, 10)),
				cmbTermType);


		cmbLanguage = new JComboBox();
		cmbLanguage.setModel(new DefaultComboBoxModel(Language.values()));
		cmbLanguage.setEditable(true);
		d = new Dimension(Math.max(100, cmbLanguage.getPreferredSize().width * 2),
				cmbLanguage.getPreferredSize().height);
		cmbLanguage.setMaximumSize(d);
		cmbLanguage.setMinimumSize(d);
		cmbLanguage.setPreferredSize(d);
		Settings settings = App.loadSettings2();

		cmbLanguage.setSelectedItem(settings.getLanguage());

		Component boxLanguage = createRow(new JLabel(bundle.getString("language")), Box.createRigidArea(new Dimension(10, 10)),
				cmbLanguage);

		Component boxTermCopy = createRow(chkPuttyLikeCopyPaste);

		defaultColorFg = new ColorSelectorButton();
		defaultColorBg = new ColorSelectorButton();
		defaultSelectionFg = new ColorSelectorButton();
		defaultSelectionBg = new ColorSelectorButton();
		defaultFoundFg = new ColorSelectorButton();
		defaultFoundBg = new ColorSelectorButton();

		cmbTermTheme = new JComboBox<>(new TerminalTheme[] { new DarkTerminalTheme(), new CustomTerminalTheme() });
//		cmbTermTheme.setSelectedIndex(0);
//		cmbTermTheme.setMaximumSize(cmbTermTheme.getPreferredSize());
//		cmbTermTheme.setMinimumSize(cmbTermTheme.getPreferredSize());

		d = new Dimension(Math.max(100, cmbTermTheme.getPreferredSize().width * 2),
				cmbTermTheme.getPreferredSize().height);
		cmbTermTheme.setMaximumSize(d);
		cmbTermTheme.setMinimumSize(d);
		cmbTermTheme.setPreferredSize(d);

		cmbTermTheme.addActionListener(e -> {
			int index = cmbTermTheme.getSelectedIndex();
			TerminalTheme theme = cmbTermTheme.getItemAt(index);
			defaultColorFg.setColor(theme.getDefaultStyle().getForeground().toAwtColor());
			defaultColorBg.setColor(theme.getDefaultStyle().getBackground().toAwtColor());

			defaultSelectionFg.setColor(theme.getSelectionColor().getForeground().toAwtColor());
			defaultSelectionBg.setColor(theme.getSelectionColor().getBackground().toAwtColor());

			defaultFoundFg.setColor(theme.getFoundPatternColor().getForeground().toAwtColor());
			defaultFoundBg.setColor(theme.getFoundPatternColor().getBackground().toAwtColor());
		});

		paletteButtons = new ColorSelectorButton[16];
		for (int i = 0; i < paletteButtons.length; i++) {
			paletteButtons[i] = new ColorSelectorButton();
		}

		cmbTermPalette = new JComboBox<>(new String[] { "xterm", "windows", "custom" });
		Dimension d1 = new Dimension(Math.max(100, cmbTermPalette.getPreferredSize().width * 2),
				cmbTermPalette.getPreferredSize().height);
		cmbTermPalette.setMaximumSize(d1);
		cmbTermPalette.setMinimumSize(d1);
		cmbTermPalette.setPreferredSize(d1);

		cmbTermPalette.addActionListener(e -> {
			int index = cmbTermPalette.getSelectedIndex();
			if (index == 2)
				return;
			ColorPalette palette = index == 0 ? ColorPalette.XTERM_PALETTE : ColorPalette.WINDOWS_PALETTE;
			Color[] colors = palette.getIndexColors();
			for (int i = 0; i < paletteButtons.length; i++) {
				paletteButtons[i].setColor(colors[i]);
			}
		});

		JPanel paletteGrid = new JPanel(new GridLayout(2, 8, 10, 10));
		for (int i = 0; i < paletteButtons.length; i++) {
			paletteGrid.add(paletteButtons[i]);
		}
		paletteGrid.setAlignmentX(Box.LEFT_ALIGNMENT);

		cmbTermTheme.setSelectedIndex(0);
		cmbTermPalette.setSelectedIndex(0);

		kcc = new KeyShortcutComponent[4];
		for (int i = 0; i < kcc.length; i++) {
			kcc[i] = new KeyShortcutComponent();
		}

		JLabel[] labels = { new JLabel(Settings.COPY_KEY), new JLabel(Settings.PASTE_KEY),
				new JLabel(Settings.CLEAR_BUFFER), new JLabel(Settings.FIND_KEY) };

		LayoutUtilities.equalizeSize(labels[0], labels[1], labels[2], labels[3]);

		Component[] kcPanels = { createRow(labels[0], kcc[0]), createRow(labels[1], kcc[1]),
				createRow(labels[2], kcc[2]), createRow(labels[3], kcc[3]) };

		Box panel = Box.createVerticalBox();

		panel.add(Box.createVerticalStrut(20));
		panel.add(createTitleLabel(bundle.getString("initial_terminal_type")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(boxTermSize);

		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("sound")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(boxTermBell);

		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("terminal_font")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(boxFontRow);

		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("misc")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(boxTermCopy);
		panel.add(Box.createVerticalStrut(5));
		panel.add(boxTermType);


		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("language")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(boxLanguage);
		panel.add(Box.createVerticalStrut(5));
		panel.add(boxLanguage);

		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("terminal_colors")));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("terminal_theme")), Box.createRigidArea(new Dimension(10, 10)), cmbTermTheme));
		panel.add(Box.createVerticalStrut(20));
		panel.add(createRow(new JLabel(bundle.getString("default_color"))));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("text")), Box.createRigidArea(new Dimension(10, 10)), defaultColorFg,
				Box.createRigidArea(new Dimension(20, 10)), new JLabel(bundle.getString("background")),
				Box.createRigidArea(new Dimension(10, 10)), defaultColorBg));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("selection_color"))));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("text")), Box.createRigidArea(new Dimension(10, 10)), defaultSelectionFg,
				Box.createRigidArea(new Dimension(20, 10)), new JLabel(bundle.getString("background")),
				Box.createRigidArea(new Dimension(10, 10)), defaultSelectionBg));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("search_pattern"))));
		panel.add(Box.createVerticalStrut(10));
		panel.add(createRow(new JLabel(bundle.getString("text")), Box.createRigidArea(new Dimension(10, 10)), defaultFoundFg,
				Box.createRigidArea(new Dimension(20, 10)), new JLabel(bundle.getString("background")),
				Box.createRigidArea(new Dimension(10, 10)), defaultFoundBg));
		panel.add(Box.createVerticalStrut(30));
		panel.add(createRow(new JLabel(bundle.getString("color_palette")), Box.createRigidArea(new Dimension(10, 10)), cmbTermPalette));
		panel.add(Box.createVerticalStrut(10));
		panel.add(paletteGrid);

		panel.add(Box.createVerticalStrut(30));
		panel.add(createTitleLabel(bundle.getString("terminal_shortcuts")));
		panel.add(Box.createVerticalStrut(10));
		for (Component cc : kcPanels) {
			panel.add(cc);
			// panel.add(Box.createVerticalStrut(10));
		}

		panel.add(Box.createVerticalGlue());
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel panelBuffered = new JPanel(new BorderLayout(), true);
		panelBuffered.add(panel);

		// UserSettingsProvider

		return new SkinnedScrollPane(panelBuffered);
	}

	public JPanel createGeneralPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		chkConfirmBeforeDelete = new JCheckBox(bundle.getString("confirm_delete_files"));
		chkConfirmBeforeMoveOrCopy = new JCheckBox(bundle.getString("confirm_move_files"));
		chkShowHiddenFilesByDefault = new JCheckBox(bundle.getString("show_hidden_files"));
		chkPromptForSudo = new JCheckBox(bundle.getString("prompt_for_sudo"));
		chkDirectoryCache = new JCheckBox(bundle.getString("directory_caching"));
		chkShowPathBar = new JCheckBox(bundle.getString("current_folder"));
		chkShowMessagePrompt = new JCheckBox(bundle.getString("show_banner"));

		chkLogWrap = new JCheckBox(bundle.getString("word_wrap"));
		spLogLinesPerPage = new JSpinner(new SpinnerNumberModel(50, 10, 500, 1));
		spLogFontSize = new JSpinner(new SpinnerNumberModel(14, 5, 500, 1));

		spSysLoadInterval = new JSpinner(new SpinnerNumberModel(3, 1, Short.MAX_VALUE, 1));

		cmbTransferMode = new JComboBox<>(new String[] { bundle.getString("transfer_normally"), bundle.getString("transfer_background") });
		cmbConflictAction = new JComboBox<>(conflictOptions);

		Dimension d1 = new Dimension(Math.max(200, cmbTransferMode.getPreferredSize().width * 2),
				cmbTransferMode.getPreferredSize().height);

		cmbTransferMode.setMaximumSize(d1);
		cmbTransferMode.setMinimumSize(d1);
		cmbTransferMode.setPreferredSize(d1);

		cmbConflictAction.setMaximumSize(d1);
		cmbConflictAction.setMinimumSize(d1);
		cmbConflictAction.setPreferredSize(d1);

		resizeNumericSpinner(spLogLinesPerPage);
		resizeNumericSpinner(spLogFontSize);
		resizeNumericSpinner(spSysLoadInterval);

		Box vbox = Box.createVerticalBox();
		chkConfirmBeforeDelete.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkConfirmBeforeMoveOrCopy.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkShowHiddenFilesByDefault.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkPromptForSudo.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkDirectoryCache.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkShowPathBar.setAlignmentX(Box.LEFT_ALIGNMENT);
		chkShowMessagePrompt.setAlignmentX(Box.LEFT_ALIGNMENT);

		chkLogWrap.setAlignmentX(Box.LEFT_ALIGNMENT);
		spLogLinesPerPage.setAlignmentX(Box.LEFT_ALIGNMENT);
		spLogFontSize.setAlignmentX(Box.LEFT_ALIGNMENT);
		spSysLoadInterval.setAlignmentX(Box.LEFT_ALIGNMENT);

//		Font font = App.SKIN.getDefaultFont().deriveFont(14.0f);
//		chkConfirmBeforeDelete.setFont(font);
//		chkConfirmBeforeMoveOrCopy.setFont(font);
//		chkShowHiddenFilesByDefault.setFont(font);
//		chkPromptForSudo.setFont(font);
//		chkDirectoryCache.setFont(font);
//		chkShowPathBar.setFont(font);
//		chkUseGlobalDarkTheme.setFont(font);
//		chkShowMessagePrompt.setFont(font);
//		spSysLoadInterval.setFont(font);
//		chkLogWrap.setFont(font);
//		spLogLinesPerPage.setFont(font);
//		spLogFontSize.setFont(font);

		vbox.add(chkConfirmBeforeDelete);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkConfirmBeforeMoveOrCopy);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkShowHiddenFilesByDefault);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkPromptForSudo);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkDirectoryCache);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkShowPathBar);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkShowMessagePrompt);
		vbox.add(Box.createRigidArea(new Dimension(10, 20)));

		JLabel lbl1 = new JLabel(bundle.getString("log_viewer_lines")), lbl2 = new JLabel(bundle.getString("log_viewer_font_size")),
				lbl3 = new JLabel(bundle.getString("system_refresh_interval"));

//		lbl1.setFont(font);
//		lbl2.setFont(font);
//		lbl3.setFont(font);

		// LayoutUtilities.equalizeSize(lbl1, lbl2, lbl3);
		LayoutUtilities.equalizeSize(spLogLinesPerPage, spLogFontSize, spSysLoadInterval);

		vbox.add(chkLogWrap);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(lbl1, Box.createHorizontalGlue(), spLogLinesPerPage));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(lbl2, Box.createHorizontalGlue(), spLogFontSize));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(lbl3, Box.createHorizontalGlue(), spSysLoadInterval));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(new JLabel(bundle.getString("transfer_mode")), Box.createHorizontalGlue(), cmbTransferMode));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(new JLabel(bundle.getString("conflict_action")), Box.createHorizontalGlue(), cmbConflictAction));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));

		vbox.setBorder(new EmptyBorder(30, 10, 10, 10));
		// add(vbox);

		panel.add(vbox);

		return panel;
	}

	static class CellRenderer extends JLabel implements ListCellRenderer<String> {

		/**
		 * 
		 */
		public CellRenderer() {
			setBorder(new EmptyBorder(15, 15, 15, 15));
			setFont(App.SKIN.getDefaultFont().deriveFont(14.0f));
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setText(value);
			if (isSelected) {
				setBackground(App.SKIN.getDefaultSelectionBackground());
				setForeground(App.SKIN.getDefaultSelectionForeground());
			} else {
				setBackground(App.SKIN.getDefaultBackground());
				setForeground(App.SKIN.getDefaultForeground());
			}
			return this;
		}

	}

	@Deprecated
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
	}

	private void applySettings() {
		Settings settings = App.getGlobalSettings();
		settings.setTerminalBell(this.chkAudibleBell.isSelected());
		settings.setPuttyLikeCopyPaste(this.chkPuttyLikeCopyPaste.isSelected());
		settings.setTermWidth((int) this.spTermWidth.getModel().getValue());
		settings.setTermHeight((int) this.spTermHeight.getModel().getValue());
		settings.setTerminalFontSize((int) this.spFontSize.getModel().getValue());
		settings.setTerminalFontName(this.cmbFonts.getSelectedItem().toString());
		settings.setTerminalType(this.cmbTermType.getSelectedItem().toString());
		settings.setLanguage((Language) this.cmbLanguage.getSelectedItem());
		settings.setTerminalTheme(this.cmbTermTheme.getSelectedItem().toString());
		settings.setTerminalPalette(this.cmbTermPalette.getSelectedItem().toString());

		for (int i = 0; i < paletteButtons.length; i++) {
			settings.getPalleteColors()[i] = paletteButtons[i].getColor().getRGB();
		}

		settings.setDefaultColorFg(this.defaultColorFg.getColor().getRGB());
		settings.setDefaultColorBg(this.defaultColorBg.getColor().getRGB());

		settings.setDefaultSelectionFg(this.defaultSelectionFg.getColor().getRGB());
		settings.setDefaultSelectionBg(this.defaultSelectionBg.getColor().getRGB());

		settings.setDefaultFoundFg(this.defaultFoundFg.getColor().getRGB());
		settings.setDefaultFoundBg(this.defaultFoundBg.getColor().getRGB());

		settings.getKeyCodeMap().put(Settings.COPY_KEY, kcc[0].getKeyCode());
		settings.getKeyCodeMap().put(Settings.PASTE_KEY, kcc[1].getKeyCode());
		settings.getKeyCodeMap().put(Settings.CLEAR_BUFFER, kcc[2].getKeyCode());
		settings.getKeyCodeMap().put(Settings.FIND_KEY, kcc[3].getKeyCode());

		settings.getKeyModifierMap().put(Settings.COPY_KEY, kcc[0].getModifier());
		settings.getKeyModifierMap().put(Settings.PASTE_KEY, kcc[1].getModifier());
		settings.getKeyModifierMap().put(Settings.CLEAR_BUFFER, kcc[2].getModifier());
		settings.getKeyModifierMap().put(Settings.FIND_KEY, kcc[3].getModifier());

		settings.setConfirmBeforeDelete(chkConfirmBeforeDelete.isSelected());
		settings.setConfirmBeforeMoveOrCopy(chkConfirmBeforeMoveOrCopy.isSelected());
		settings.setShowHiddenFilesByDefault(chkShowHiddenFilesByDefault.isSelected());
		settings.setPromptForSudo(chkPromptForSudo.isSelected());
		settings.setDirectoryCache(chkDirectoryCache.isSelected());
		settings.setShowPathBar(chkShowPathBar.isSelected());
		settings.setShowMessagePrompt(chkShowMessagePrompt.isSelected());
		settings.setUseGlobalDarkTheme(chkUseGlobalDarkTheme.isSelected());

		settings.setLogViewerFont((Integer) spLogFontSize.getValue());
		settings.setLogViewerLinesPerPage((Integer) spLogLinesPerPage.getValue());
		settings.setLogViewerUseWordWrap(chkLogWrap.isSelected());

		settings.setSysloadRefreshInterval((Integer) spSysLoadInterval.getValue());

		settings.setEditors(editorModel.getEntries());

		settings.setManualScaling(chkUseManualScaling.isSelected());
		settings.setUiScaling((double) spScaleValue.getValue());

		//settings.setUsingMasterPassword(chkUseMasterPassword.isSelected());

		App.saveSettings();
		super.setVisible(false);
	}

	public boolean showDialog(JFrame window) {
		return this.showDialog(window, null);
	}

	public boolean showDialog(JFrame window, SettingsPageName page) {
		this.setLocationRelativeTo(window);
		Settings settings = App.getGlobalSettings();

		if (page != null) {
			navList.setSelectedIndex(page.index);
		}

		loadSettings(settings);

		super.setVisible(true);
		return false;
	}

	private void loadSettings(Settings settings) {
		this.chkAudibleBell.setSelected(settings.isTerminalBell());
		this.chkPuttyLikeCopyPaste.setSelected(settings.isPuttyLikeCopyPaste());

		this.spTermWidth.setValue(settings.getTermWidth());
		this.spTermHeight.setValue(settings.getTermHeight());
		this.spFontSize.setValue(settings.getTerminalFontSize());

		this.cmbFonts.setSelectedItem(settings.getTerminalFontName());
		this.cmbTermType.setSelectedItem(settings.getTerminalType());

		this.cmbTermTheme.setSelectedItem(settings.getTerminalTheme());

		int[] colors = settings.getPalleteColors();

		for (int i = 0; i < paletteButtons.length; i++) {
			paletteButtons[i].setColor(new Color(colors[i]));
		}

		this.cmbTermPalette.setSelectedItem(settings.getTerminalPalette());

		kcc[0].setKeyCode(settings.getKeyCodeMap().get(Settings.COPY_KEY));
		kcc[1].setKeyCode(settings.getKeyCodeMap().get(Settings.PASTE_KEY));
		kcc[2].setKeyCode(settings.getKeyCodeMap().get(Settings.CLEAR_BUFFER));
		kcc[3].setKeyCode(settings.getKeyCodeMap().get(Settings.FIND_KEY));

		kcc[0].setModifier(settings.getKeyModifierMap().get(Settings.COPY_KEY));
		kcc[1].setModifier(settings.getKeyModifierMap().get(Settings.PASTE_KEY));
		kcc[2].setModifier(settings.getKeyModifierMap().get(Settings.CLEAR_BUFFER));
		kcc[3].setModifier(settings.getKeyModifierMap().get(Settings.FIND_KEY));

		chkConfirmBeforeDelete.setSelected(settings.isConfirmBeforeDelete());
		chkConfirmBeforeMoveOrCopy.setSelected(settings.isConfirmBeforeMoveOrCopy());
		chkShowHiddenFilesByDefault.setSelected(settings.isShowHiddenFilesByDefault());
		chkPromptForSudo.setSelected(settings.isPromptForSudo());
		chkDirectoryCache.setSelected(settings.isDirectoryCache());
		chkShowPathBar.setSelected(settings.isShowPathBar());
		chkShowMessagePrompt.setSelected(settings.isShowMessagePrompt());
		chkUseGlobalDarkTheme.setSelected(settings.isUseGlobalDarkTheme());

		spLogFontSize.setValue(settings.getLogViewerFont());
		spLogLinesPerPage.setValue(settings.getLogViewerLinesPerPage());
		chkLogWrap.setSelected(settings.isLogViewerUseWordWrap());

		spSysLoadInterval.setValue(settings.getSysloadRefreshInterval());

		cmbTransferMode.addActionListener(e -> {
			if (cmbTransferMode.getSelectedIndex() == 0) {
				conflictOptions.removeAllElements();
				conflictOptions.addAll(conflictOption1);
				cmbConflictAction.setSelectedIndex(3);
			} else {
				conflictOptions.removeAllElements();
				conflictOptions.addAll(conflictOption2);
				cmbConflictAction.setSelectedIndex(0);
			}
		});

		cmbTransferMode.setSelectedIndex(settings.getFileTransferMode() == TransferMode.Normal ? 0 : 1);
		// set initial values
		//conflictOptions.addAll(conflictOption1);

		switch (settings.getConflictAction()) {
		case OverWrite:
			cmbConflictAction.setSelectedIndex(0);
			break;
		case AutoRename:
			cmbConflictAction.setSelectedIndex(1);
			break;
		case Skip:
			cmbConflictAction.setSelectedIndex(2);
			break;
		case Prompt:
			cmbConflictAction.setSelectedIndex(3);
			break;
		default:
			break;
		}

		this.editorModel.clear();
		this.editorModel.addEntries(settings.getEditors());

		this.chkUseManualScaling.setSelected(settings.isManualScaling());
		this.spScaleValue.setValue(settings.getUiScaling());

		this.chkUseMasterPassword.setSelected(settings.isUsingMasterPassword());
		this.btnChangeMasterPassword.setEnabled(settings.isUsingMasterPassword());

	}

//	private String[] getTerminalFonts() {
//		String fontNames[] = new String[FontUtils.TERMINAL_FONTS.size()];
//		int c = 0;
//		for (String font : FontUtils.TERMINAL_FONTS.keySet()) {
//			Font ttf = FontUtils.loadFont(String.format("/fonts/terminal/%s.ttf", font));
//			fontNames[c++] = ttf.getFontName();
//		}
//		return fontNames;
//	}

	public JPanel createEditorPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(new EmptyBorder(30, 10, 10, 10));

		editorTable = new JTable(editorModel);
		panel.add(new SkinnedScrollPane(editorTable));
		Box box = Box.createHorizontalBox();
		JButton btnAddEditor = new JButton(bundle.getString("add_editor"));
		JButton btnDelEditor = new JButton(bundle.getString("remove_editor"));
		box.add(Box.createHorizontalGlue());
		box.add(btnAddEditor);
		box.add(Box.createHorizontalStrut(10));
		box.add(btnDelEditor);
		panel.add(box, BorderLayout.SOUTH);

		btnAddEditor.addActionListener(e -> {
			JFileChooser jfc = new JFileChooser();
			if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				JTextField txt = new SkinnedTextField(30);
				txt.setText(file.getName());
				String name = OptionPaneUtils.showInputDialog(this, bundle.getString("editor_name"), file.getName(), bundle.getString("add_editor2"));
				if (name != null) {
					editorModel.addEntry(new EditorEntry(name, file.getAbsolutePath()));
				}
			}
		});
		btnDelEditor.addActionListener(e -> {
			int index = editorTable.getSelectedRow();
			if (index != -1) {
				editorModel.deleteEntry(index);
			}
		});
		return panel;
	}

	private Component createMiscPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		chkUseManualScaling = new JCheckBox(bundle.getString("zoom_text"));
		spScaleValue = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 100.0, 0.01));
		resizeNumericSpinner(spScaleValue);

		chkUseGlobalDarkTheme = new JCheckBox(bundle.getString("global_dark_theme"));
		chkUseGlobalDarkTheme.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box vbox = Box.createVerticalBox();
		chkUseManualScaling.setAlignmentX(Box.LEFT_ALIGNMENT);
		vbox.add(chkUseManualScaling);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(createRow(new JLabel(bundle.getString("zoom_percentage")), Box.createHorizontalGlue(), spScaleValue));
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(chkUseGlobalDarkTheme);
		vbox.setBorder(new EmptyBorder(30, 10, 10, 10));
		// add(vbox);

		panel.add(vbox);

		return panel;
	}

	private Component createSecurityPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		chkUseMasterPassword = new JCheckBox(bundle.getString("use_master_password"));
		btnChangeMasterPassword = new JButton(bundle.getString("change_master_password"));

		chkUseMasterPassword.addActionListener(e -> {
			if (chkUseMasterPassword.isSelected()) {
				char[] password = promptPassword();
				if (password == null) {
					chkUseMasterPassword.setSelected(false);
					btnChangeMasterPassword.setEnabled(false);
					return;
				}
				try {
					if(!PasswordStore.getSharedInstance().changeStorePassword(password)) {
						throw new Exception(bundle.getString("change_password_failed"));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, bundle.getString("error_operation"));
				}
				App.getGlobalSettings().setUsingMasterPassword(true);
				App.saveSettings();
				JOptionPane.showMessageDialog(this, bundle.getString("password_aes"));
			} else {
				try {
					PasswordStore.getSharedInstance().changeStorePassword(new char[0]);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, bundle.getString("error_operation"));
				}
				App.getGlobalSettings().setUsingMasterPassword(false);
				App.saveSettings();
				JOptionPane.showMessageDialog(this, bundle.getString("password_unprotected"));
			}
		});

		chkUseMasterPassword.setAlignmentX(Box.LEFT_ALIGNMENT);
		btnChangeMasterPassword.setAlignmentX(Box.LEFT_ALIGNMENT);

		Box vbox = Box.createVerticalBox();
		vbox.add(chkUseMasterPassword);
		vbox.add(Box.createRigidArea(new Dimension(10, 10)));
		vbox.add(btnChangeMasterPassword);
		vbox.setBorder(new EmptyBorder(30, 10, 10, 10));
		panel.add(vbox);

		return panel;
	}

	private char[] promptPassword() {
		JPasswordField pass1 = new JPasswordField(30);
		JPasswordField pass2 = new JPasswordField(30);
		while (JOptionPane.showOptionDialog(this,
				new Object[] { bundle.getString("new_master_password"), pass1, bundle.getString("reenter_master_password"), pass2 }, bundle.getString("master_password"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {
			char[] password1 = pass1.getPassword();
			char[] password2 = pass2.getPassword();

			String reason = "";

			boolean passwordOK = false;
			if (password1.length == password2.length && password1.length > 0) {
				passwordOK = true;
				for (int i = 0; i < password1.length; i++) {
					if (password1[i] != password2[i]) {
						passwordOK = false;
						reason = bundle.getString("password_no_match");
						break;
					}
				}
			} else {
				reason = bundle.getString("password_no_match");
			}

			if (!passwordOK) {
				JOptionPane.showMessageDialog(this, reason);
			} else {
				return password1;
			}

			pass1.setText("");
			pass2.setText("");
		}

		return null;
	}

//	private void resizeTextField(JTextField txt) {
//		txt.setText("WW");
//		Dimension d = txt.getPreferredSize();
//		txt.setText("");
//		txt.setPreferredSize(d);
//		txt.setMaximumSize(d);
//		txt.setMinimumSize(d);
//	}
}
