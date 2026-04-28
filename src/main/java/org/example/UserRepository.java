package org.example;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

public final class UserRepository {
    private UserRepository() {
    }

    public static void ensureSchema() throws SQLException {
        try {
            HibernateUtil.getSessionFactory();
        } catch (RuntimeException e) {
            throw new SQLException("Failed to initialize Hibernate schema", e);
        }
    }

    public static boolean createUser(String login, String email, String passwordHash) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.openSession()) {
            transaction = session.beginTransaction();

            UserEntity user = new UserEntity(login, email, passwordHash);
            session.persist(user);

            transaction.commit();
            return true;
        } catch (ConstraintViolationException e) {
            rollbackQuietly(transaction);
            if (isDuplicateConstraint(e)) {
                return false;
            }
            throw new SQLException("Failed to create user", e);
        } catch (RuntimeException e) {
            rollbackQuietly(transaction);
            if (isDuplicateConstraint(e)) {
                return false;
            }
            throw new SQLException("Failed to create user", e);
        }
    }

    public static boolean authenticate(String login, String password) throws SQLException {
        String hql = "select u from UserEntity u where u.login = :login";
        try (Session session = HibernateUtil.openSession()) {
            Query<UserEntity> query = session.createQuery(hql, UserEntity.class);
            query.setParameter("login", login);
            query.setMaxResults(1);

            List<UserEntity> users = query.getResultList();
            if (users.isEmpty()) {
                return false;
            }

            String storedPassword = users.get(0).getPassword();
            return PasswordService.matches(password, storedPassword);
        } catch (RuntimeException e) {
            throw new SQLException("Failed to authenticate user", e);
        }
    }

    private static void rollbackQuietly(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        if (transaction.getStatus().canRollback()) {
            transaction.rollback();
        }
    }

    private static boolean isDuplicateConstraint(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof SQLIntegrityConstraintViolationException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
