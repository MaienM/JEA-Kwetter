package servlets;

import com.google.gson.Gson;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;
import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "TweetServlet", urlPatterns = "/tweets")
public class TweetServlet extends HttpServlet {
    @PersistenceContext(unitName = "main")
    private EntityManager em;

    @Inject private KwetterService service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Create a simplified view of the tweets
        List<Map<String, Object>> data = new ArrayList<>();
        //for (Tweet tweet : service.getRecentTweets()) {
        for (Tweet tweet : (List<Tweet>) em.createQuery("SELECT tweet FROM Tweet tweet").getResultList()) {
            //tweet = em.merge(tweet);

            Map<String, Object> innerData = new HashMap<>();
            innerData.put("tweet", tweet.getContent());
            innerData.put("user", tweet.getUser().getUsername());
            //innerData.put("tags", tweet.getHashtags().stream().map(Hashtag::getName).collect(Collectors.toList()));
            //innerData.put("mentions", tweet.getMentioned().stream().map(User::getUsername).collect(Collectors.toList()));
            data.add(innerData);
        }

        // Convert to json and return
        Gson gson = new Gson();
        gson.toJson(data, response.getWriter());
    }
}
