package database.daos;

import database.models.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

/**
 * Created by Michon on 3/15/2016.
 */
public class UserDAO {
    @Inject private EntityManager em;

    public void save(User user) {
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
;
    }

    public User findByUsername(String username) {
        try {
            return (User) em.createQuery("SELECT user FROM User user WHERE user.username = :username").setParameter("username", username).getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
        catch (NonUniqueResultException ignored) {
            return null;
        }
    }
}
