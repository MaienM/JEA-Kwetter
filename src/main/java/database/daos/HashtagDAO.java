package database.daos;

import database.models.Hashtag;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

/**
 * Created by Michon on 3/15/2016.
 */
public class HashtagDAO {
    @Inject private EntityManager em;

    public void save(Hashtag tag) {
        em.getTransaction().begin();
        em.persist(tag);
        em.getTransaction().commit();
    }

    public Hashtag find(String name) {
        try {
            return (Hashtag) em.createQuery("SELECT tag FROM Hashtag tag WHERE tag.name = :name").setParameter("name", name).getSingleResult();
        }
        catch (NoResultException ignored) {
            return null;
        }
    }

    public Hashtag findOrCreate(String name) {
        Hashtag tag = find(name);
        if (tag == null) {
            tag = new Hashtag(name);
            save(tag);
        }
        return tag;
    }
}
