package by.itacademy.hibernate.dao;


import by.itacademy.hibernate.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.DateOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.Year;
import java.util.Collections;
import java.util.List;

import static by.itacademy.hibernate.entity.QCompany.company;
import static by.itacademy.hibernate.entity.QPayment.payment;
import static by.itacademy.hibernate.entity.QUser.user;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Возвращает всех сотрудников
     */
    public List<User> findAll(Session session) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch();
    }

    /**
     * Возвращает всех сотрудников с указанным именем
     */
    public List<User> findAllByFirstName(Session session, String firstName) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo().firstname.eq(firstName))
                .fetch();
    }

    /**
     * Возвращает первые {limit} сотрудников, упорядоченных по дате рождения (в порядке возрастания)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, int limit) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .orderBy(user.personalInfo().birthDate.asc())
                .limit(limit)
                .fetch();
    }

    /**
     * Возвращает всех сотрудников компании с указанным названием
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .join(user.company())
                .where(user.company().name.eq(companyName))
                .fetch();
    }

    /**
     * Возвращает все выплаты, полученные сотрудниками компании с указанными именем,
     * упорядоченные по имени сотрудника, а затем по размеру выплаты
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .where(company.name.eq(companyName))
                .orderBy(user.personalInfo().firstname.asc(),
                        payment.amount.asc())
                .fetch();
    }

    /**
     * Возвращает среднюю зарплату сотрудника с указанными именем и фамилией
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .where(user.personalInfo().firstname.eq(firstName),
                        user.personalInfo().lastname.eq(lastName))
                .fetchOne();
    }

    /**
     * Возвращает для каждой компании: название, среднюю зарплату всех её сотрудников. Компании упорядочены по названию.
     */
    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return new JPAQuery<Tuple>(session)
                .select(company.name,
                        payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .orderBy(company.name.asc())
                .fetch();
    }

    /**
     * Возвращает список: сотрудник (объект User), средний размер выплат, но только для тех сотрудников, чей средний размер выплат
     * больше среднего размера выплат всех сотрудников
     * Упорядочить по имени сотрудника
     */
    public List<Tuple> isItPossible(Session session) {
        return new JPAQuery<Tuple>(session)
                .select(user,
                        payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .having(payment.amount.avg().gt(
                                new JPAQuery<Double>(session)
                                        .select(payment.amount.avg())
                                        .from(payment)
                        )
                )
                .groupBy(user.id)
                .orderBy(user.personalInfo().firstname.asc())
                .fetch();
    }

    /**
     * Средние выплаты по возврастам сотрудников
     */
    public List<Tuple> findAveragePaymentAmountByBirthDate(Session session) {
        DateOperation<LocalDate> birthDateToYear = Expressions.dateOperation(
                LocalDate.class,
                Ops.DateTimeOps.DATE,
                user.personalInfo().birthDate
        );
        return new JPAQuery<Tuple>(session)
                .select(birthDateToYear.year(),
                        payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .groupBy(birthDateToYear.year())
                .orderBy(birthDateToYear.year().asc())
                .fetch();
    }

    /**
     * Сумма всех выплат по имени сотрудника
     */
    public Integer findSumPaymentsAmountByFirstName(Session session, String firstName) {
        return new JPAQuery<Integer>(session)
                .select(payment.amount.sum())
                .from(user)
                .join(user.payments, payment)
                .where(user.personalInfo().firstname.eq(firstName))
                .fetchOne();
    }

    /**
     * Всех сотрудников, у которых есть хотя бы 1 выплата больше Х
     */
    public List<User> findUsersByPaymentsMoreThan(Session session, Integer minPaymentAmount) {
        return new JPAQuery<User>(session)
                .select(user)
                .from(payment)
                .join(payment.receiver(), user)
                .where(payment.amount.gt(minPaymentAmount))
                .groupBy(user)
                .fetch();
    }


    public static UserDao getInstance() {
        return INSTANCE;
    }
}