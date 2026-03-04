window.fr_univlorraine_ecandidat_vaadin_form_quilleditor_QuillEditor = function() {
    var e = this.getElement();
    e.innerHTML = '<div class="quill-container" style="height: 300px;"></div>';
	e.style.borderRadius = "4px";
	
    var container = e.querySelector('.quill-container');
	
	var toolbarOptions = [
	        [{ 'header': [1, 2, 3, 4, false] }],        // Titres
	        [{ 'font': [] }],                            // Polices
	        ['bold', 'italic', 'underline', 'strike'],   // Style de texte
	        [{ 'color': [] }, { 'background': [] }],     // Couleurs texte/fond
	        [{ 'script': 'sub'}, { 'script': 'super' }], // Indice/Exposant
	        [{ 'list': 'ordered'}, { 'list': 'bullet' }],// Listes
	        [{ 'align': [] }],                           // Alignement
	        ['blockquote', 'code-block'],                // Citations/Code
	        ['link', 'image'],                  // Médias
	        ['clean']                                    // Bouton pour effacer le formatage
	    ];

    // Initialisation de Quill
    var quill = new Quill(container, {
        theme: 'snow',
        placeholder: 'Écrivez ici...',
        theme: 'snow',
		modules: {
            toolbar: toolbarOptions
        },
        placeholder: 'Écrivez votre contenu ici...'
    });

    // Envoyer les changements vers le serveur Java
    quill.on('text-change', function() {
        var html = quill.root.innerHTML;
        this.onTextChange(html);
    }.bind(this));

    // Fonction appelée depuis le Java pour modifier le texte
    this.onStateChange = function() {
        var state = this.getState();
        if (quill.root.innerHTML !== state.text) {
            quill.root.innerHTML = state.text || "";
        }
    };
};