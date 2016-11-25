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
public class Article implements Serializable{
    
    private int NumSerie;
    private String Nom;
    private String Marque;
    private float prix;
    private String EtatPaiement;
    private String Type;
    private int Quantite;

    public Article() {
    }

    public int getNumSerie() {
        return NumSerie;
    }

    public void setNumSerie(int NumSerie) {
        this.NumSerie = NumSerie;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int Quantite) {
        this.Quantite = Quantite;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String Nom) {
        this.Nom = Nom;
    }

    public String getMarque() {
        return Marque;
    }

    public void setMarque(String Marque) {
        this.Marque = Marque;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    public String getEtatPaiement() {
        return EtatPaiement;
    }

    public void setEtatPaiement(String EtatPaiement) {
        this.EtatPaiement = EtatPaiement;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }
    
    
    
    
}
