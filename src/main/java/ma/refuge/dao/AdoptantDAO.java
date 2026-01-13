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

    public void update(Adoptant a) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.update(a);
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
            return s.createQuery("SELECT DISTINCT a FROM Adoptant a LEFT JOIN FETCH a.animauxAdoptes", Adoptant.class).list();
        }
    }
    
    public boolean aDesAnimauxAdoptes(int adoptantId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(a) > 0 FROM Animal a WHERE a.adoptant.id = :adoptantId";
            return s.createQuery(hql, Boolean.class)
                   .setParameter("adoptantId", adoptantId)
                   .uniqueResult();
        }
    }
}
