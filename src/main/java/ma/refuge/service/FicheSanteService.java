package ma.refuge.service;

import ma.refuge.dao.AnimalDAO;
import ma.refuge.dao.FicheSanteDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Animal;
import ma.refuge.model.FicheSante;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;

public class FicheSanteService {

    private final FicheSanteDAO ficheSanteDAO = new FicheSanteDAO();
    private final HistoriqueDAO historiqueDAO = new HistoriqueDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();

    public void ajouterFicheSante(int animalId, FicheSante fiche) {
        Animal animal = animalDAO.findById(animalId);
        if (animal == null) {
            throw new IllegalArgumentException("Animal introuvable pour ID: " + animalId);
        }
        fiche.setAnimal(animal);
        ficheSanteDAO.save(fiche);

        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("FICHE SANTE");
        h.setDescription("Ajout fiche santé pour animal ID : " + animalId);
        historiqueDAO.save(h);
    }
}
