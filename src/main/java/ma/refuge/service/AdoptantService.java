package ma.refuge.service;

import ma.refuge.dao.AdoptantDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;
import java.util.List;

public class AdoptantService {

    private final AdoptantDAO adoptantDAO = new AdoptantDAO();
    private final HistoriqueDAO historiqueDAO = new HistoriqueDAO();

    public void ajouterAdoptant(Adoptant adoptant) {
        adoptantDAO.save(adoptant);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("AJOUT ADOPTANT");
        h.setDescription("Ajout adoptant : " + adoptant.getNom());
        historiqueDAO.save(h);
    }

    public void modifierAdoptant(Adoptant adoptant) {
        adoptantDAO.update(adoptant);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("MODIFICATION ADOPTANT");
        h.setDescription("Modification adoptant : " + adoptant.getNom());
        historiqueDAO.save(h);
    }

    public void supprimerAdoptant(int id) {
        adoptantDAO.deleteById(id);
        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("SUPPRESSION ADOPTANT");
        h.setDescription("Suppression adoptant ID : " + id);
        historiqueDAO.save(h);
    }

    public List<Adoptant> listerAdoptants() {
        return adoptantDAO.findAll();
    }
    
    public boolean aDesAnimauxAdoptes(int adoptantId) {
        return adoptantDAO.aDesAnimauxAdoptes(adoptantId);
    }
}
