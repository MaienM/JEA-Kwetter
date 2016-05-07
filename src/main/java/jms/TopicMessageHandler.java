package jms;

import services.KwetterService;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/tweets/topic"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
})
public class TopicMessageHandler implements MessageListener {
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

        service.createTweet(service.getUser(tweetMessage.getUsername()), "T: " + tweetMessage.getContent());
    }
}
