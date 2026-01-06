package ma.refuge.util;

public class test {
    public static void main(String[] args) {
        HibernateUtil.getSessionFactory().openSession();
        System.out.println("✅ Connexion Hibernate OK avec MySQL XAMPP");
    }
}

