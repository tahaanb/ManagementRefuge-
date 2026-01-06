package ma.refuge.service;

import ma.refuge.dao.AnimalDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Animal;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;
import java.util.List;

public class AnimalService {

    private final AnimalDAO animalDAO = new AnimalDAO();
    private final HistoriqueDAO historiqueDAO = new HistoriqueDAO();

    public void ajouterAnimal(Animal animal) {
        animalDAO.save(animal);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("AJOUT ANIMAL");
        h.setDescription("Ajout de l'animal : " + animal.getNom());
        historiqueDAO.save(h);
    }

    public void modifierAnimal(Animal animal) {
        animalDAO.update(animal);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("MODIFICATION ANIMAL");
        h.setDescription("Modification de l'animal : " + animal.getNom());
        historiqueDAO.save(h);
    }

    public void supprimerAnimal(int id) {
        animalDAO.deleteById(id);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("SUPPRESSION ANIMAL");
        h.setDescription("Suppression de l'animal ID : " + id);
        historiqueDAO.save(h);
    }

    public List<Animal> listerAnimaux() {
        return animalDAO.findAll();
    }
}
