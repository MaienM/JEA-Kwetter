package database.daos;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


abstract public class BaseDAO<T> {
/*
    @Inject protected EntityManager em;

    public void save(T obj) {
        em.getTransaction().begin();
        em.persist(obj);
        em.getTransaction().commit();
    }
/*/
    @PersistenceContext(unitName = "main")
    protected EntityManager em;

    public void save(T obj) {
        em.persist(obj);
    }
//*/
}
