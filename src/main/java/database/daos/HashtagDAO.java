package database.daos;

import database.models.Hashtag;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

@Named
@Stateless
public class HashtagDAO extends BaseDAO<Hashtag> {
    public Hashtag findByName(String name) {
        try {
            return (Hashtag) em.createQuery("SELECT tag FROM Hashtag tag WHERE tag.name = :name").setParameter("name", name).getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
        catch (NonUniqueResultException ignored) {
            return null;
        }
    }

    public Hashtag findOrCreateByName(String name) {
        Hashtag tag = findByName(name);
        if (tag == null) {
            tag = new Hashtag(name);
            save(tag);
        }
        return tag;
    }
}
