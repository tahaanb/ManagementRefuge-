package ma.refuge.service;

import ma.refuge.dao.HistoriqueDAO;
import ma.refuge.model.Historique;

import java.util.List;

public class HistoriqueService {

    private HistoriqueDAO historiqueDAO = new HistoriqueDAO();

    public List<Historique> getHistorique() {
        return historiqueDAO.findAll();
    }
}
