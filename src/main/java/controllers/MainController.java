package controllers;


import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;
import services.KwetterService;
import sun.rmi.runtime.Log;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.nio.charset.Charset;
import java.nio.file.attribute.UserPrincipal;
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
}
