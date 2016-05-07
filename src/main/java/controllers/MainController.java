package controllers;


import com.sun.javaws.exceptions.ExitException;
import com.sun.javaws.exceptions.InvalidArgumentException;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;
import services.JMSService;
import services.KwetterService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewScoped
@ManagedBean(name = "main", eager = true)
public class MainController {
    @Inject
    private KwetterService service;

    @Inject
    private JMSService jmsService;

    private boolean currentUserSet = false;
    private User currentUser = null;
    private boolean randomUserSet = false;
    private User randomUser = null;
    private List<Tweet> tweets;

    public User getCurrentUser() {
        if (!currentUserSet) {
            String username = null;

            Principal userPrincipal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
            if (userPrincipal != null) {
                // Get the logged in user.
                username = userPrincipal.getName();
            }
            else {
                // No logged in user, so get the user from the url.
                Map<String, String> parameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
                username = parameterMap.get("user");
            }

            // Get the user, if possible.
            if (username != null) {
                currentUser = service.getUser(username);
            }
            currentUserSet = true;
        }
        return currentUser;
    }

    public User getRandomUser() {
        if (!randomUserSet) {
            randomUser = service.getRandomUser();
            randomUserSet = true;
        }
        return randomUser;
    }

    public List<User> getUsers() {
        return service.getUsers();
    }

    public List<Hashtag> getTrends() {
        return service.getTrends();
    }

    public long getCurrentUserTweetCount() {
        return service.getTweetCountForUser(getCurrentUser());
    }

    public List<Tweet> getCurrentUserTweets() {
        return service.getRecentTweetsFromUser(getCurrentUser());
    }

    public List<Tweet> getCurrentUserTimelineTweets() {
        Set<Tweet> tweets = new TreeSet<>();
        tweets.addAll(getCurrentUserTweets());
        tweets.addAll(service.getRecentTweetsFollows(getCurrentUser()));
        return new ArrayList<>(tweets).subList(0, Math.min(50, tweets.size()));
    }

    public List<Tweet> getCurrentUserMentionedTweets() {
        return service.getRecentTweetsMentioning(getCurrentUser());
    }

    public List<Tweet> getTweets() {
        if (tweets == null) {
            tweets = getCurrentUserTimelineTweets();
        }
        return tweets;
    }

    public String getQuery() {
        return "";
    }

    public void setQuery(String query) {
        if (query.isEmpty()) {
            tweets = null;
        } else {
            tweets = service.findTweetsByText(query);
        }
    }

    public String getPost() {
        return "";
    }

    public void setPost(String content) {
        tweets.add(0, service.createTweet(getCurrentUser(), content));
    }

    public void setHashtag(String tag) {
        tweets = service.getRecentTweetsHashtag(tag);
    }

    public String getHash(String input) throws NoSuchAlgorithmException {
        String email = input.toLowerCase() + "@kwetter.com";
        MessageDigest md = MessageDigest.getInstance("MD5");
        return (new HexBinaryAdapter()).marshal(md.digest(email.getBytes())).toLowerCase();
    }

    public boolean isLoggedin() {
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal() != null;
    }

    public String logout() {
        User user = getCurrentUser();

        try {
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
            request.logout();
        } catch (ServletException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Logout failure", e);
        }

        return "/user-tweets.xhtml?faces-redirect=true&user=" + user.getUsername();
    }

    // Jms

    private String jmsContent;
    private User jmsUser;
    private String jmsMethod;

    public String getJmsContent() {
        return "";
    }

    public void setJmsContent(String content) {
        jmsContent = content;
    }

    public String getJmsUser() {
        return null;
    }

    public void setJmsUser(String user) {
        jmsUser = service.getUser(user);
    }

    public String getJmsMethod() {
        return null;
    }

    public void setJmsMethod(String method) {
        jmsMethod = method;
    }

    public void doJmsPost() throws Exception {
        switch (jmsMethod) {
            case "topic":
                jmsService.createTweetTopic(jmsUser, jmsContent);
                break;

            case "queue":
                jmsService.createTweetQueue(jmsUser, jmsContent);
                break;

            default:
                throw new Exception("Unknown jms method: " + jmsMethod);
        }

        jmsUser = null;
        jmsContent = null;
    }
}
