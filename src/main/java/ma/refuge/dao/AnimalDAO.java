package ma.refuge.dao;

import ma.refuge.model.Animal;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class AnimalDAO {

    public void save(Animal animal) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.save(animal);
            s.getTransaction().commit();
        }
    }

    public void update(Animal animal) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            s.update(animal);
            s.getTransaction().commit();
        }
    }

    public Animal findById(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Animal.class, id);
        }
    }

    public void deleteById(int id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            s.beginTransaction();
            Animal a = s.get(Animal.class, id);
            if (a != null) s.delete(a);
            s.getTransaction().commit();
        }
    }

    public List<Animal> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("SELECT DISTINCT a FROM Animal a LEFT JOIN FETCH a.adoptant", Animal.class).list();
        }
    }
}
