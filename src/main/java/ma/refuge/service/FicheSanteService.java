package ma.refuge.service;

import ma.refuge.dao.FicheSanteDAO;
import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.FicheSante;
import ma.refuge.model.Historique;

import java.time.LocalDateTime;

public class FicheSanteService {

    private FicheSanteDAO ficheSanteDAO = new FicheSanteDAO();
    private HistoriqueDAO historiqueDAO = new HistoriqueDAO();

    public void ajouterFicheSante(FicheSante fiche) {
        ficheSanteDAO.save(fiche);

        Historique h = new Historique();
        h.setDate(LocalDateTime.now());
        h.setAction("FICHE SANTE");
        h.setDescription("Ajout fiche santé pour animal ID : "
                + fiche.getAnimal().getId());

        historiqueDAO.save(h);
    }
}
