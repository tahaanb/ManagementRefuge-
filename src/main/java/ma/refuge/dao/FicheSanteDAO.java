package ma.refuge.dao;

import ma.refuge.model.FicheSante;
import ma.refuge.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class FicheSanteDAO {

    public FicheSante findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(FicheSante.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FicheSante> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM FicheSante", FicheSante.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> findByAnimalId(int animalId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE f.animal.id = :animalId ORDER BY f.date DESC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            query.setParameter("animalId", animalId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> findByType(FicheSante.TypeConsultation type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE f.type = :type ORDER BY f.date DESC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            query.setParameter("type", type);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> findByDateBetween(LocalDate debut, LocalDate fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE f.date BETWEEN :debut AND :fin ORDER BY f.date DESC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> findByDateRappelBetween(LocalDate debut, LocalDate fin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE f.dateProchainRappel IS NOT NULL " +
                        "AND f.dateProchainRappel BETWEEN :debut AND :fin " +
                        "ORDER BY f.dateProchainRappel ASC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> searchByKeyword(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE " +
                        "LOWER(f.diagnostic) LIKE :keyword OR " +
                        "LOWER(f.traitement) LIKE :keyword OR " +
                        "LOWER(f.observations) LIKE :keyword OR " +
                        "LOWER(f.veterinaire) LIKE :keyword " +
                        "ORDER BY f.date DESC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            query.setParameter("keyword", searchPattern);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<FicheSante> findByVeterinaire(String nomVeterinaire) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM FicheSante f WHERE LOWER(f.veterinaire) LIKE :veterinaire ORDER BY f.date DESC";
            Query<FicheSante> query = session.createQuery(hql, FicheSante.class);
            query.setParameter("veterinaire", "%" + nomVeterinaire.toLowerCase() + "%");
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void save(FicheSante fiche) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(fiche);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'enregistrement de la fiche de santé", e);
        }
    }

    public void update(FicheSante fiche) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.update(fiche);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la fiche de santé", e);
        }
    }

    public void delete(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            FicheSante fiche = session.get(FicheSante.class, id);
            if (fiche != null) {
                session.delete(fiche);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de la fiche de santé", e);
        }
    }
}
