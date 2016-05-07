package jms;

import services.KwetterService;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/tweets/queue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class QueueMessageHandler implements MessageListener {
    @Inject
    private KwetterService service;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        TweetMessage tweetMessage;
        try {
            tweetMessage = (TweetMessage) objectMessage.getObject();
        } catch (JMSException e) {
            e.printStackTrace();
            return;
        }

        service.createTweet(service.getUser(tweetMessage.getUsername()), "Q: " + tweetMessage.getContent());
    }
}
