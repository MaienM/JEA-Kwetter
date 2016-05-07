package services;

import database.models.User;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named
@Stateless
@ApplicationScoped
public class JMSService {
    /**
     * The pre-configured GlassFish-default connection factory
     */
    private static final String JNDI_CONNECTION_FACTORY = "jms/__defaultConnectionFactory";

    /**
     * The JNDI name under which your {@link Topic} should be: you have to
     * create the topic before running this class
     */
    private static final String JNDI_TOPIC = "jms/tweets/topic";

    @Inject
    @JMSConnectionFactory(JNDI_CONNECTION_FACTORY)
    JMSContext ctx;

    /**
     * @param <T> the return type
     * @param retvalClass the returned value's {@link Class}
     * @param jndi the JNDI path to the resource
     * @return the resource at the specified {@code jndi} path
     */
    private <T> T lookup(Class<T> retvalClass, String jndi) {
        try {
            return retvalClass.cast(InitialContext.doLookup(jndi));
        } catch (NamingException ex) {
            throw new IllegalArgumentException("failed to lookup instance of " + retvalClass + " at " + jndi, ex);
        }
    }

    /**
     * Create a tweet
     *
     * @param user The user that posted the tweet
     * @param content The text of the tweet
     */
    public void createTweet(User user, String content) {
        final Topic topic = lookup(Topic.class, JNDI_TOPIC);
        final JMSProducer producer = ctx.createProducer();
    }
}
