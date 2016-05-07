package servlets.endpoints;

import controllers.MainController;
import database.models.User;
import services.KwetterService;

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FollowersEndpoint {
    @Endpoint("/users/([^/]+)/followers")
    public static Object getFollowers(KwetterService service, String[] params) {
        // Get the users followers and follows
        User user = service.getUser(params[0]);
        Set<User> followers = service.getFollowers(user);
        List<Long> followingIds = service.getFollows(user).stream().map(User::getId).collect(Collectors.toList());

        // Create a list with the followers + whether we also follow them
        List<Map<String, Object>> returnData = new ArrayList<>();
        for (User follower : followers) {
            Map<String, Object> data = new HashMap<>();
            data.put("following", followingIds.contains(follower.getId()));
            data.put("user", follower);
            returnData.add(data);
        }

        return returnData;
    }
}
