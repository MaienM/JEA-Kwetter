package database.daos;

import database.models.Hashtag;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * Created by Michon on 3/15/2016.
 */
public class HashtagDAO {
    @Inject private EntityManager em;

    public Hashtag findOrCreate(String name) {
        Hashtag tag = (Hashtag) em.createQuery("SELECT OBJECT(tag) FROM Hashtag WHERE tag.name = :name").setParameter("name", name).getSingleResult();
        if (tag == null) {
            tag = new Hashtag(name);
        }
        return tag;
    }
}
