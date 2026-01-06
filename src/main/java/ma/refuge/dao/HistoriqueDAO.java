package ma.refuge.dao;

import ma.refuge.model.Historique;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class HistoriqueDAO {

    public void save(Historique h) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.save(h);
        s.getTransaction().commit();
        s.close();
    }

    public List<Historique> findAll() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Historique> list = s.createQuery("from Historique order by date desc", Historique.class).list();
        s.close();
        return list;
    }
}
