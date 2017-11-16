esup-ecandidat
===================

Plus d'information sur https://www.esup-portail.org/wiki/display/PROJESUPOPI/eCandidat+V2

Initialisation du projet
--------------------------------
<li>Récupérez les sources
<li>Dupliquez les fichiers .sample et les adaptez les
<li>Ajoutez à votre settings.xml Maven le repository Oracle (voir astuce sur cette page : https://www.esup-portail.org/wiki/pages/viewpage.action?pageId=501547020)

Démarrez votre projet
--------------------------------
Dans votre répertoire ecandidat lancez la commande Maven : 
>mvn clean  tomcat7:run -P oracle-enable

Publiez une release
----------------------------
Dans votre répertoire ecandidat lancez la commande Maven : 
>mvn clean compile package -P oracle-enable