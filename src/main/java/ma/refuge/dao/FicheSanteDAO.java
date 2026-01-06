package ma.refuge.dao;

import ma.refuge.model.FicheSante;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;

public class FicheSanteDAO {

    public void save(FicheSante f) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.save(f);
            s.getTransaction().commit();
        }
    }
}
