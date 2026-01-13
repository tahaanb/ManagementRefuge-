package ma.refuge.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique")
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime date;
    private String action;
    private String description;
    private String status;

    public Historique() {}

    // Constructeur avec paramètres
    public Historique(LocalDateTime date, String action, String description, String status) {
        this.date = date;
        this.action = action;
        this.description = description;
        this.status = status;
    }

    /* Getters & Setters */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
