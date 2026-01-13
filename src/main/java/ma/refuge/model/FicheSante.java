package ma.refuge.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "fiche_sante")
public class FicheSante {
    public enum TypeConsultation {
        VACCINATION,
        CONSULTATION,
        TRAITEMENT,
        CHIRURGIE,
        CONTROLE,
        AUTRE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "document_path")
    private String documentPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeConsultation type;

    private String veterinaire;
    private String diagnostic;
    private String traitement;
    private String observations;
    private LocalDate dateProchainRappel;
    private String notesImportantes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    public FicheSante() {
        this.date = LocalDate.now();
        this.type = TypeConsultation.CONSULTATION; // Valeur par défaut
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public TypeConsultation getType() { 
        return type; 
    }
    
    public void setType(TypeConsultation type) { 
        this.type = type;
    }
    
    public StringProperty typeProperty() {
        // Retourne une nouvelle propriété avec la valeur actuelle de type
        // ou une chaîne vide si type est null
        return new SimpleStringProperty(type != null ? type.toString() : "");
    }

    public String getVeterinaire() { return veterinaire; }
    public void setVeterinaire(String veterinaire) { this.veterinaire = veterinaire; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getTraitement() { return traitement; }
    public void setTraitement(String traitement) { this.traitement = traitement; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public LocalDate getDateProchainRappel() { return dateProchainRappel; }
    public void setDateProchainRappel(LocalDate dateProchainRappel) { this.dateProchainRappel = dateProchainRappel; }

    public String getNotesImportantes() { return notesImportantes; }
    public void setNotesImportantes(String notesImportantes) { this.notesImportantes = notesImportantes; }

    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }


    @Override
    public String toString() {
        return String.format("%s - %s (%s)", 
            date.toString(), 
            type.toString(),
            veterinaire != null ? veterinaire : "Sans vétérinaire");
    }
}
