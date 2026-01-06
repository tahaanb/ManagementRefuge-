package ma.refuge.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "fiche_sante")
public class FicheSante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;
    private String description;

    @Column(name = "document_path")
    private String documentPath;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public FicheSante() {}

    /* Getters & Setters */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
}
