package ma.refuge.service;

import ma.refuge.dao.AnimalDAO;
import ma.refuge.dao.FicheSanteDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Animal;
import ma.refuge.model.FicheSante;
import ma.refuge.model.Historique;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FicheSanteService {

    private final FicheSanteDAO ficheSanteDAO = new FicheSanteDAO();
    private final HistoriqueDAO historiqueDAO = new HistoriqueDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();

    public FicheSante trouverFicheParId(int id) {
        return ficheSanteDAO.findById(id);
    }

    public List<FicheSante> listerToutesLesFiches() {
        return ficheSanteDAO.findAll();
    }

    public List<FicheSante> listerParAnimal(int animalId) {
        return ficheSanteDAO.findByAnimalId(animalId);
    }

    public List<FicheSante> listerParType(FicheSante.TypeConsultation type) {
        return ficheSanteDAO.findByType(type);
    }

    public List<FicheSante> listerParPeriode(LocalDate debut, LocalDate fin) {
        return ficheSanteDAO.findByDateBetween(debut, fin);
    }

    public List<FicheSante> listerRappelsAVenir(int joursAvant) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dateLimite = aujourdhui.plusDays(joursAvant);
        return ficheSanteDAO.findByDateRappelBetween(aujourdhui, dateLimite);
    }

    public void ajouterFicheSante(int animalId, FicheSante fiche) {
        validerFicheSante(fiche);
        Animal animal = animalDAO.findById(animalId);
        if (animal == null) {
            throw new IllegalArgumentException("Animal introuvable pour ID: " + animalId);
        }
        fiche.setAnimal(animal);
        ficheSanteDAO.save(fiche);
        
        enregistrerHistorique("AJOUT FICHE SANTÉ", 
                            "Ajout fiche santé #" + fiche.getId() + " pour " + animal.getNom());
    }

    public void mettreAJourFicheSante(FicheSante fiche) {
        validerFicheSante(fiche);
        FicheSante existante = ficheSanteDAO.findById(fiche.getId());
        if (existante == null) {
            throw new IllegalArgumentException("Fiche de santé introuvable pour ID: " + fiche.getId());
        }
        
        ficheSanteDAO.update(fiche);
        enregistrerHistorique("MISE À JOUR FICHE SANTÉ", 
                            "Mise à jour fiche santé #" + fiche.getId());
    }

    public void supprimerFicheSante(int id) {
        FicheSante fiche = ficheSanteDAO.findById(id);
        if (fiche != null) {
            ficheSanteDAO.delete(id);
            enregistrerHistorique("SUPPRESSION FICHE SANTÉ", 
                                "Suppression fiche santé #" + id);
        }
    }

    private void validerFicheSante(FicheSante fiche) {
        if (fiche == null) {
            throw new IllegalArgumentException("La fiche de santé ne peut pas être nulle");
        }
        if (fiche.getType() == null) {
            throw new IllegalArgumentException("Le type de consultation est obligatoire");
        }
        if (fiche.getDate() == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        if (fiche.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date ne peut pas être dans le futur");
        }
    }

    private void enregistrerHistorique(String action, String description) {
        Historique historique = new Historique();
        historique.setDate(LocalDateTime.now());
        historique.setAction(action);
        historique.setDescription(description);
        historiqueDAO.save(historique);
    }

    public List<FicheSante> rechercherParMotCle(String motCle) {
        return ficheSanteDAO.searchByKeyword(motCle);
    }

    public List<FicheSante> listerParVeterinaire(String nomVeterinaire) {
        return ficheSanteDAO.findByVeterinaire(nomVeterinaire);
    }
}
