package by.itacademy.hibernate.dao;


import by.itacademy.hibernate.entity.Company;
import by.itacademy.hibernate.utils.TestDataImporter;
import by.itacademy.hibernate.entity.Payment;
import by.itacademy.hibernate.entity.User;
import by.itacademy.hibernate.util.HibernateUtil;
import com.querydsl.core.Tuple;
import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class UserDaoTest {

    private final SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    private final UserDao userDao = UserDao.getInstance();

    @BeforeAll
    public void initDb() {
        TestDataImporter.importData(sessionFactory);
    }

    @AfterAll
    public void finish() {
        sessionFactory.close();
    }

    @Test
    void findAll() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAll(session);
        assertThat(results).hasSize(5);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Bill Gates", "Steve Jobs", "Sergey Brin", "Tim Cook", "Diane Greene");

        session.getTransaction().commit();
    }

    @Test
    void findAllByFirstName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAllByFirstName(session, "Bill");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).fullName()).isEqualTo("Bill Gates");

        session.getTransaction().commit();
    }

    @Test
    void findLimitedUsersOrderedByBirthday() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        int limit = 3;
        List<User> results = userDao.findLimitedUsersOrderedByBirthday(session, limit);
        assertThat(results).hasSize(limit);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).contains("Diane Greene", "Steve Jobs", "Bill Gates");

        session.getTransaction().commit();
    }

    @Test
    void findAllByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> results = userDao.findAllByCompanyName(session, "Google");
        assertThat(results).hasSize(2);

        List<String> fullNames = results.stream().map(User::fullName).collect(toList());
        assertThat(fullNames).containsExactlyInAnyOrder("Sergey Brin", "Diane Greene");

        session.getTransaction().commit();
    }

    @Test
    void findAllPaymentsByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Payment> applePayments = userDao.findAllPaymentsByCompanyName(session, "Apple");
        assertThat(applePayments).hasSize(5);

        List<Integer> amounts = applePayments.stream().map(Payment::getAmount).collect(toList());
        assertThat(amounts).contains(250, 500, 600, 300, 400);

        session.getTransaction().commit();
    }

    @Test
    void findAveragePaymentAmountByFirstAndLastNames() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Double averagePaymentAmount = userDao.findAveragePaymentAmountByFirstAndLastNames(session, "Bill", "Gates");
        assertThat(averagePaymentAmount).isEqualTo(300.0);

        session.getTransaction().commit();
    }

    @Test
    void findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Tuple> results = userDao.findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(session);
        assertThat(results).hasSize(3);

        List<String> orgNames = results.stream().map(a -> a.get(0, String.class)).collect(toList());
        assertThat(orgNames).contains("Apple", "Google", "Microsoft");

        List<Double> orgAvgPayments = results.stream().map(a -> a.get(1, Double.class)).collect(toList());
        assertThat(orgAvgPayments).contains(410.0, 400.0, 300.0);

        session.getTransaction().commit();
    }

    @Test
    void isItPossible() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Tuple> results = userDao.isItPossible(session);
        assertThat(results).hasSize(2);

        List<String> names = results.stream().map(r -> r.get(0, User.class)).map(User::fullName).collect(toList());
        assertThat(names).contains("Sergey Brin", "Steve Jobs");

        List<Double> averagePayments = results.stream().map(r -> r.get(1, Double.class)).collect(toList());
        assertThat(averagePayments).contains(500.0, 450.0);

        session.getTransaction().commit();
    }

    @Test
    void findAveragePaymentAmountByBirthDate() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Tuple> results = userDao.findAveragePaymentAmountByBirthDate(session);
        assertThat(results).hasSize(3);

        List<Integer> years = results.stream()
                .map(r -> r.get(0, Integer.class))
                .collect(toList());
        assertThat(years).contains(1955, 1960, 1973);

        List<Double> averagePayments = results.stream().map(r -> r.get(1, Double.class)).collect(toList());
        assertThat(averagePayments).contains(350.0, 350.0, 500.0);

        session.getTransaction().commit();
    }

    @Test
    void findSumPaymentsAmountByFirstName() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Integer result = userDao.findSumPaymentsAmountByFirstName(session, "Steve");
        assertThat(result).isEqualTo(1350);

        session.getTransaction().commit();
    }

    @Test
    void findUsersByPaymentAmountMoreThan() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> users = userDao.findUsersByPaymentAmountMoreThan(session, 300);
        assertThat(users).hasSize(4);

        List<String> names = users.stream().map(User::fullName).collect(toList());
        assertThat(names).contains("Bill Gates", "Steve Jobs", "Sergey Brin", "Tim Cook");

        session.getTransaction().commit();
    }

    @Test
    void findUsersByPaymentCountMoreThan() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<User> users = userDao.findUsersByPaymentCountMoreThan(session, 2);
        assertThat(users).hasSize(4);

        List<String> names = users.stream().map(User::fullName).collect(toList());
        assertThat(names).contains("Bill Gates", "Steve Jobs", "Sergey Brin", "Diane Greene");

        session.getTransaction().commit();
    }

    @Test
    void findSumCompaniesPayments() {
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Tuple> results = userDao.findSumCompaniesPayments(session);
        assertThat(results).hasSize(3);

        List<String> companyNames = results.stream()
                .map(t -> t.get(0, Company.class).getName())
                .toList();
        assertThat(companyNames).contains("Microsoft", "Apple", "Google");

        List<Integer> paymentAmount = results.stream()
                .map(t -> t.get(1, Integer.class))
                .collect(toList());
        assertThat(paymentAmount).contains(2400, 2050, 900);

        session.getTransaction().commit();
    }
}
