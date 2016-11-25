/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RequeteClient;

import java.io.Serializable;

/**
 *
 * @author Alex
 */
public class Client implements Serializable {
    private int NumClient;
    private String Nom;
    private String Prenom;
    private String Adresse;
    private String NumTel;
    private String Email;
    private String Login;
    private String PassWord;

    public int getNumClient() {
        return NumClient;
    }

    public void setNumClient(int NumClient) {
        this.NumClient = NumClient;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String Nom) {
        this.Nom = Nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String Prenom) {
        this.Prenom = Prenom;
    }

    public String getAdresse() {
        return Adresse;
    }

    public void setAdresse(String Adresse) {
        this.Adresse = Adresse;
    }

    public String getNumTel() {
        return NumTel;
    }

    public void setNumTel(String NumTel) {
        this.NumTel = NumTel;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public void setPassWord(String PassWord) {
        this.PassWord = PassWord;
    }
    
    public String getLogin() {
        return Login;
    }

    public String getPassWord() {
        return PassWord;
    }
}
