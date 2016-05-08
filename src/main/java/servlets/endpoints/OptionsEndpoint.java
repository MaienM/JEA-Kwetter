package servlets.endpoints;

import services.KwetterService;

import javax.servlet.http.HttpServletRequest;

public class OptionsEndpoint {
    @Endpoint(value = "/.*", method = Endpoint.Method.OPTIONS)
    public static Object getTweet(HttpServletRequest request, KwetterService service, String[] params) {
        return new EndpointResponse(EndpointResponse.Status.OK, null);
    }
}
