package fr.univlorraine.ecandidat.controllers.rest;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RestUser implements Serializable {

	private String login;
	private String libelle;
	private String mail;

	public RestUser() {
		super();
	}

	public RestUser(final String login, final String libelle, final String mail) {
		super();
		this.login = login;
		this.libelle = libelle;
		this.mail = mail;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(final String libelle) {
		this.libelle = libelle;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(final String mail) {
		this.mail = mail;
	}

}
