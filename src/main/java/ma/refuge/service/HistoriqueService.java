package ma.refuge.service;

import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HistoriqueService {

    private HistoriqueDAO historiqueDAO = new HistoriqueDAO();
    private final Random random = new Random();
    private final List<String> actions = Arrays.asList(
        "Création fiche santé",
        "Mise à jour fiche santé",
        "Suppression fiche santé",
        "Ajout document",
        "Mise à jour document",
        "Suppression document"
    );
    
    private final List<String> statusList = Arrays.asList(
        "Terminé",
        "En cours",
        "En attente"
    );

    public List<Historique> getHistorique() {
        List<Historique> historiqueList = historiqueDAO.findAll();
        
        // Si la base de données est vide, on génère des données de démonstration
        if (historiqueList.isEmpty()) {
            return generateDemoData();
        }
        
        return historiqueList;
    }
    
    private List<Historique> generateDemoData() {
        List<Historique> demoData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Générer 50 entrées d'historique de démonstration
        for (int i = 0; i < 50; i++) {
            String action = actions.get(random.nextInt(actions.size()));
            String status = statusList.get(random.nextInt(statusList.size()));
            String description = "Action " + (i + 1) + " - " + action.toLowerCase();
            LocalDateTime date = now.minusHours(random.nextInt(720)); // Jusqu'à 30 jours en arrière
            
            Historique historique = new Historique(date, action, description, status);
            demoData.add(historique);
        }
        
        return demoData;
    }
    
    // Méthode pour ajouter une entrée dans l'historique
    public void ajouterEntree(Historique historique) {
        historiqueDAO.save(historique);
    }
}
