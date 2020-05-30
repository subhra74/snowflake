package muon.app.ui.laf;

import java.awt.Color;

public class AppSkinDark extends AppSkin {

	/**
	 * 
	 */
	public AppSkinDark() {
		initDefaultsDark();
	}

	private void initDefaultsDark() {

		Color selectionColor = new Color(3, 155, 229);
		Color controlColor = new Color(40, 44, 52);
		Color textColor = new Color(230, 230, 230);
		Color selectedTextColor = new Color(230, 230, 230);
		Color infoTextColor = new Color(180, 180, 180);
		Color borderColor = new Color(24, 26, 31);
		Color treeTextColor = new Color(75 + 20, 83 + 20, 98 + 20);
		Color scrollbarColor = new Color(75, 83, 98);
		Color scrollbarRolloverColor = new Color(75 + 20, 83 + 20, 98 + 20);
		Color textFieldColor = new Color(40 + 10, 44 + 10, 52 + 10);

		Color buttonGradient1 = new Color(57, 62, 74);
		Color buttonGradient2 = new Color(55 - 10, 61 - 10, 72 - 10);
		Color buttonGradient3 = new Color(57 + 20, 62 + 20, 74 + 20);
		Color buttonGradient4 = new Color(57 + 10, 62 + 10, 74 + 10);
		Color buttonGradient5 = new Color(57 - 20, 62 - 20, 74 - 20);
		Color buttonGradient6 = new Color(57 - 10, 62 - 10, 74 - 10);

		this.defaults.put("nimbusBase", controlColor);
		this.defaults.put("nimbusSelection", selectionColor);
		this.defaults.put("textBackground", selectionColor);
		this.defaults.put("textHighlight", selectionColor);
		this.defaults.put("desktop", selectionColor);
		this.defaults.put("nimbusFocus", selectionColor);

		this.defaults.put("ArrowButton.foreground", textColor);

		this.defaults.put("nimbusSelectionBackground", selectionColor);
		this.defaults.put("nimbusSelectedText", selectedTextColor);
		this.defaults.put("control", controlColor);
		this.defaults.put("nimbusBorder", borderColor);
		this.defaults.put("Table.alternateRowColor", controlColor);
		this.defaults.put("nimbusLightBackground", textFieldColor);

		this.defaults.put("tabSelectionBackground", scrollbarColor);
		this.defaults.put("Table.background", buttonGradient6);
		this.defaults.put("Table[Enabled+Selected].textForeground", selectedTextColor);

//		this.defaults.put("scrollbar", buttonGradient4);
//		this.defaults.put("scrollbar-hot", buttonGradient3);

		this.defaults.put("text", textColor);
		this.defaults.put("menuText", textColor);
		this.defaults.put("controlText", textColor);
		this.defaults.put("textForeground", textColor);
		this.defaults.put("infoText", infoTextColor);

		this.defaults.put("List.foreground", textColor);
		this.defaults.put("List.background", controlColor);
		this.defaults.put("List[Disabled].textForeground", selectedTextColor);
		this.defaults.put("List[Selected].textBackground", selectionColor);
		

		this.defaults.put("Label.foreground", textColor);

		this.defaults.put("Tree.background", textFieldColor);
		this.defaults.put("Tree.textForeground", treeTextColor);

		this.defaults.put("scrollbar", scrollbarColor);
		this.defaults.put("scrollbar-hot", scrollbarRolloverColor);

		this.defaults.put("button.normalGradient1", buttonGradient1);
		this.defaults.put("button.normalGradient2", buttonGradient2);
		this.defaults.put("button.hotGradient1", buttonGradient3);
		this.defaults.put("button.hotGradient2", buttonGradient4);
		this.defaults.put("button.pressedGradient1", buttonGradient5);
		this.defaults.put("button.pressedGradient2", buttonGradient6);

		this.defaults.put("TextField.background", textFieldColor);
		this.defaults.put("FormattedTextField.background", textFieldColor);
		this.defaults.put("PasswordField.background", textFieldColor);

		createSkinnedButton(this.defaults);
		createTextFieldSkin(this.defaults);
		createSpinnerSkin(this.defaults);
		createComboBoxSkin(this.defaults);
		createTreeSkin(this.defaults);
		createTableHeaderSkin(this.defaults);
		createPopupMenuSkin(this.defaults);
		createCheckboxSkin(this.defaults);
		createRadioButtonSkin(this.defaults);
		createTooltipSkin(this.defaults);
		createSkinnedToggleButton(this.defaults);
		createProgressBarSkin(this.defaults);

		this.defaults.put("ScrollBarUI", CustomScrollBarUI.class.getName());
	}
}
