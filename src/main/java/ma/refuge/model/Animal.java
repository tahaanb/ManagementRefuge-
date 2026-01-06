package ma.refuge.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nom;
    private String espece;
    private String race;
    private int age;

    @Column(name = "statut_adoption")
    private String statutAdoption;

    @Column(name = "photo_path")
    private String photoPath;

    @ManyToOne
    @JoinColumn(name = "adoptant_id")
    private Adoptant adoptant;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL)
    private List<FicheSante> fichesSante;

    public Animal() {}

    /* Getters & Setters */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEspece() { return espece; }
    public void setEspece(String espece) { this.espece = espece; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getStatutAdoption() { return statutAdoption; }
    public void setStatutAdoption(String statutAdoption) { this.statutAdoption = statutAdoption; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public Adoptant getAdoptant() { return adoptant; }
    public void setAdoptant(Adoptant adoptant) { this.adoptant = adoptant; }

    public List<FicheSante> getFichesSante() { return fichesSante; }
    public void setFichesSante(List<FicheSante> fichesSante) { this.fichesSante = fichesSante; }
}
