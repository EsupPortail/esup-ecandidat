package fr.univlorraine.ecandidat.vaadin.form.quilleditor;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

@SuppressWarnings("serial")
public class QuillEditorField extends CustomField<String> {

	private final QuillEditor editor = new QuillEditor();

	public QuillEditorField() {
		editor.addValueChangeListener(newValue -> {
			// Remonte la valeur dans le CustomField
			setValue(editor.getValue());
		});
	}

	@Override
	protected Component initContent() {
		return editor;
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	@Override
	protected void setInternalValue(String newValue) {
		super.setInternalValue(newValue);
		editor.setValue(newValue == null ? "" : newValue);
	}

	public void setEditorHeight(String height) {
		editor.setHeight(height);
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		editor.setReadOnly(readOnly);
	}

	@Override
	public String toString() {
		return getValue();
	}
}