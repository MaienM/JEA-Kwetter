package database.daos;

import database.models.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Named
@Stateless
public class UserDAO extends BaseDAO<User> {
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

    public User findRandom() {
        try {
            return (User) em.createQuery("SELECT user FROM User user ORDER BY random()").setMaxResults(1).getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
    }

    public List<User> getAll() {
        return (List<User>) em.createQuery("SELECT user FROM User user").setMaxResults(50).getResultList();
    }
}
