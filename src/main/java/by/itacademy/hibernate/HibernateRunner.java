package by.itacademy.hibernate;

import by.itacademy.hibernate.entity.*;
import by.itacademy.hibernate.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class HibernateRunner {

    public static void main(String[] args) {

/*
        Company company = Company.builder()
                .name("Google1")
                .build();
        PersonalInfo personalInfo = PersonalInfo.builder()
                .firstname("Ivan")
                .lastname("Ivanov")
                .birthDate(new Birthday(LocalDate.of(2000, 1, 19)))
                .build();

        User user = User.builder()
                .username("ivan5@gmail.com")
                .personalInfo(personalInfo)
                .role(Role.ADMIN)
                .company(company)
                .build();

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {
                Transaction transaction = session1.beginTransaction();
                //session1.saveOrUpdate(company);
                session1.saveOrUpdate(user);
//                User user1 = session1.get(User.class, 1L);
//                session1.evict(user1);
                session1.getTransaction().commit();
            }

        }*/
    }
}
