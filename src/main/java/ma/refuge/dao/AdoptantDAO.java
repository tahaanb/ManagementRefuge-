package ma.refuge.dao;

import ma.refuge.model.Adoptant;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class AdoptantDAO {

    public void save(Adoptant a) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.save(a);
        s.getTransaction().commit();
        s.close();
    }

    public List<Adoptant> findAll() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Adoptant> list = s.createQuery("from Adoptant", Adoptant.class).list();
        s.close();
        return list;
    }
}
