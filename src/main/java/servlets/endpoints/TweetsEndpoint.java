package servlets.endpoints;

import services.KwetterService;

public class TweetsEndpoint {
    @Endpoint("/tweets")
    public static Object getTweets(KwetterService service, String[] params) {
        return service.getRecentTweets();
    }

    @Endpoint("/tweets/([0-9]+)")
    public static Object getTweet(KwetterService service, String[] params) {
        return service.getTweet(Long.valueOf(params[0]));
    }
}
