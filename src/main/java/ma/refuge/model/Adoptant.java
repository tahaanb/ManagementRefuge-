package ma.refuge.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "adoptant")
public class Adoptant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String prenom;
    private String telephone;
    private String email;

    @OneToMany(mappedBy = "adoptant", fetch = FetchType.LAZY)
    private List<Animal> animauxAdoptes;

    public Adoptant() {}

    public Adoptant(String nom, String prenom, String telephone, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
    }





/* Getters & Setters */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Animal> getAnimauxAdoptes() { return animauxAdoptes; }
    public void setAnimauxAdoptes(List<Animal> animauxAdoptes) { this.animauxAdoptes = animauxAdoptes; }
}
