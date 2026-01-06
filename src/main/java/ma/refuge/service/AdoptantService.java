package ma.refuge.service;

import ma.refuge.dao.AdoptantDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Adoptant;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;
import java.util.List;

public class AdoptantService {

    private AdoptantDAO adoptantDAO = new AdoptantDAO();
    private HistoriqueDAO historiqueDAO = new HistoriqueDAO();

    public void ajouterAdoptant(Adoptant adoptant) {
        adoptantDAO.save(adoptant);

        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("AJOUT ADOPTANT");
        h.setDescription("Ajout adoptant : " + adoptant.getNom());

        historiqueDAO.save(h);
    }

    public List<Adoptant> getTousLesAdoptants() {
        return adoptantDAO.findAll();
    }
}
