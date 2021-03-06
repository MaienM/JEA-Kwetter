package servlets.endpoints;

import database.models.User;
import flexjson.JSONDeserializer;
import services.KwetterService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public class ProfileEndpoint {
    @Endpoint("/profile/([^/]+)")
    public static Object getProfile(HttpServletRequest request, KwetterService service, String[] params) {
        User user = service.getUser(params[0]);
        if (user == null) {
            return new EndpointResponse(EndpointResponse.Status.INVALID, "Unknown user");
        }

        return user;
    }

    @Endpoint(value = "/profile/([^/]+)", method = Endpoint.Method.PUT)
    public static Object putProfile(HttpServletRequest request, KwetterService service, String[] params) throws IOException {
        User user = service.getUser(params[0]);
        if (user == null) {
            return new EndpointResponse(EndpointResponse.Status.INVALID, "Unknown user");
        }

        JSONDeserializer<Map<String, String>> deserializer = new JSONDeserializer<>();
        Map<String, String> data = deserializer.deserialize(request.getReader());

        String oldUsername = user.getUsername();
        String username = data.get("username");
        if (!user.getUsername().equals(username) && service.getUser(username) != null) {
            return new EndpointResponse(EndpointResponse.Status.IM_A_TEAPOT, "Username already in use");
        }

        user.setUsername(username);
        user.setBio(data.get("bio"));
        user.setLocation(data.get("location"));
        user.setWebsite(data.get("website"));
        user.setPicture(data.get("picture"));
        service.saveUser(oldUsername, user);

        return user;
    }
}
