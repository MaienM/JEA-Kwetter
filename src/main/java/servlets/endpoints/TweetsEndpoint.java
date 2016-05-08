package servlets.endpoints;

import services.KwetterService;

import javax.servlet.http.HttpServletRequest;

public class TweetsEndpoint {
    @Endpoint("/tweets")
    public static Object getTweets(HttpServletRequest request, KwetterService service, String[] params) {
        return service.getRecentTweets();
    }

    @Endpoint("/tweets/([0-9]+)")
    public static Object getTweet(HttpServletRequest request, KwetterService service, String[] params) {
        return service.getTweet(Long.valueOf(params[0]));
    }
}
