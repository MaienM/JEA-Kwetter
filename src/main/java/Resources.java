import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

public class Resources {
    @PersistenceContext(unitName = "main")
    @Produces
    static private EntityManager em;
}
