package muon.app.ui.components.session.files;

import java.awt.Component;

import javax.swing.ComboBoxEditor;

import muon.app.ui.components.SkinnedTextField;

public class AddressBarComboBoxEditor extends SkinnedTextField implements ComboBoxEditor {

	public AddressBarComboBoxEditor() {
		super.putClientProperty("paintNoBorder", "True");
	}

	@Override
	public Component getEditorComponent() {
		return this;
	}

	@Override
	public void setItem(Object anObject) {
		if (anObject != null) {
			this.setText(anObject.toString());
		}
	}

	@Override
	public Object getItem() {
		return this.getText();
	}

}
