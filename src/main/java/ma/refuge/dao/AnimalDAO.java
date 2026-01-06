package ma.refuge.dao;

import ma.refuge.model.Animal;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class AnimalDAO {

    public void save(Animal animal) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.save(animal);
        s.getTransaction().commit();
        s.close();
    }

    public void update(Animal animal) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.update(animal);
        s.getTransaction().commit();
        s.close();
    }

    public void delete(Animal animal) {
        Session s = HibernateUtil.getSessionFactory().openSession();
        s.beginTransaction();
        s.delete(animal);
        s.getTransaction().commit();
        s.close();
    }

    public List<Animal> findAll() {
        Session s = HibernateUtil.getSessionFactory().openSession();
        List<Animal> list = s.createQuery("from Animal", Animal.class).list();
        s.close();
        return list;
    }
}
