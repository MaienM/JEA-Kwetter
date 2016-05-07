package servlets.endpoints;

import database.models.User;
import services.KwetterService;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FollowsEndpoint {
    @Endpoint("/users/([^/]+)/follows")
    public static Object getFollows(KwetterService service, String[] params) {
        // Get the users followers and follows
        User user = service.getUser(params[0]);
        Set<User> follows = service.getFollows(user);
        List<Long> followersIds = service.getFollowers(user).stream().map(User::getId).collect(Collectors.toList());

        // Create a list with the follows + whether they also follow us
        List<Map<String, Object>> returnData = new ArrayList<>();
        for (User followee : follows) {
            Map<String, Object> data = new HashMap<>();
            data.put("followed_by", followersIds.contains(followee.getId()));
            data.put("user", followee);
            returnData.add(data);
        }

        return returnData;
    }

    @Endpoint(value = "/users/([^/]+)/follows/([^/]+)", method = Endpoint.Method.POST)
    public static Object addFollow(KwetterService service, String[] params) {
        User user = service.getUser(params[0]);
        User followee = service.getUser(params[1]);
        if (user == null || followee == null) {
            return new EndpointResponse(EndpointResponse.Status.INVALID, "Unknown user");
        }

        service.addFollow(user, followee);

        return new EndpointResponse(EndpointResponse.Status.NO_CONTENT, null);
    }

    @Endpoint(value = "/users/([^/]+)/follows/([^/]+)", method = Endpoint.Method.DELETE)
    public static Object removeFollow(KwetterService service, String[] params) {
        User user = service.getUser(params[0]);
        User followee = service.getUser(params[1]);
        if (user == null || followee == null) {
            return new EndpointResponse(EndpointResponse.Status.INVALID, "Unknown user");
        }

        service.removeFollow(user, followee);

        return new EndpointResponse(EndpointResponse.Status.NO_CONTENT, null);
    }
}
