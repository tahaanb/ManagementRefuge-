package ma.refuge.dao;

import ma.refuge.model.Adoptant;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class AdoptantDAO {

    public void save(Adoptant a) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.save(a);
            s.getTransaction().commit();
        }
    }

    public Adoptant findById(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Adoptant.class, id);
        }
    }

    public void deleteById(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            Adoptant a = s.get(Adoptant.class, id);
            if (a != null) s.delete(a);
            s.getTransaction().commit();
        }
    }

    public List<Adoptant> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from Adoptant", Adoptant.class).list();
        }
    }
}
