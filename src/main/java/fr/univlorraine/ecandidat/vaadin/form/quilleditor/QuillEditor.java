package fr.univlorraine.ecandidat.vaadin.form.quilleditor;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

@SuppressWarnings("serial")
@StyleSheet("vaadin://addons/quilleditor/quill.snow.css")
@JavaScript({
	"vaadin://addons/quilleditor/quill.min.js",
	"vaadin://addons/quilleditor/quilleditor-connector.js"
})
public class QuillEditor extends AbstractJavaScriptComponent {

	// --- Gestion de l'événement de changement de valeur ---

	public interface ValueChangeListener extends Serializable {
		void valueChange(ValueChangeEvent event);
	}

	public static class ValueChangeEvent extends Component.Event {
		public ValueChangeEvent(Component source) {
			super(source);
		}
	}

	private static final Method VALUE_CHANGE_METHOD = ReflectTools.findMethod(ValueChangeListener.class, "valueChange", ValueChangeEvent.class);

	// --- Constructeur ---

	public QuillEditor() {
		// Réception des données venant du JavaScript (Navigateur -> Serveur)
		addFunction("onTextChange", arguments -> {
			// Utilisation de getString(0) pour extraire la valeur du JsonArray
			String html = arguments.getString(0);

			// Mise à jour de l'état interne sans renvoyer au client (évite les boucles)
			if (html != null && !html.equals(getState().text)) {
				getState().text = html;
				// Déclenchement de l'événement côté Java
				fireEvent(new ValueChangeEvent(this));
			}
		});
	}

	// --- API Publique ---

	public void setValue(String html) {
		if (html == null || html.isEmpty()) {
			getState().text = "";
			return;
		}

		// 1. Remplacement des divs par des paragraphes
		String cleanedHtml = html.replace("<div", "<p").replace("</div", "</p");

		// 2. Nettoyage des paragraphes vides résiduels (optionnel)
		cleanedHtml = cleanedHtml.replace("<p></p>", "");

		// 3. Suppression des espaces blancs inutiles entre les balises
		cleanedHtml = cleanedHtml.trim();

		getState().text = cleanedHtml;
	}

	public String getValue() {
		return getState().text;
	}

	public void addValueChangeListener(ValueChangeListener listener) {
		addListener(ValueChangeEvent.class, listener, VALUE_CHANGE_METHOD);
	}

	public void removeValueChangeListener(ValueChangeListener listener) {
		removeListener(ValueChangeEvent.class, listener, VALUE_CHANGE_METHOD);
	}

	// --- État partagé (State) ---

	@Override
	protected QuillState getState() {
		return (QuillState) super.getState();
	}

	public static class QuillState extends JavaScriptComponentState {
		public String text = "";
	}
}