package services;

import database.models.Tweet;
import database.models.User;
import jms.TweetMessage;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Logger;

@Named
@Stateless
@ApplicationScoped
public class JMSService {
    @Inject
    @JMSConnectionFactory("jms/__defaultConnectionFactory")
    JMSContext ctx;

    @Resource
    private SessionContext sc;

    @Resource(lookup = "jms/tweets/topic")
    private Topic topic;

    @Resource(lookup = "jms/tweets/queue")
    private Queue queue;

    /**
     * Create a tweet using the topic method
     *
     * @param user The user that posted the tweet
     * @param content The text of the tweet
     */
    public void createTweetTopic(User user, String content) {
        JMSProducer producer = ctx.createProducer();
        producer.send(topic, new TweetMessage(user, content));
    }

    /**
     * Create a tweet using the queue method
     *
     * @param user The user that posted the tweet
     * @param content The text of the tweet
     */
    public void createTweetQueue(User user, String content) {
        JMSProducer producer = ctx.createProducer();
        producer.send(queue, new TweetMessage(user, content));
    }
}
