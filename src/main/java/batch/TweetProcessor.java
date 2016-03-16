package batch;

import database.daos.HashtagDAO;
import database.daos.UserDAO;
import database.models.Hashtag;
import database.models.User;
import services.KwetterService;

import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class TweetProcessor implements ItemProcessor {
    public class TweetProcessorInfo {
        public final String user;
        public final String tweet;
        public final List<String> mentions = new ArrayList<>();
        public final List<String> hashtags = new ArrayList<>();

        public TweetProcessorInfo(String user, String tweet) {
            this.user = user;
            this.tweet = tweet;
        }
    }

    @Inject private JobContext jobContext;

    @Override
    public Object processItem(Object o) throws Exception {
        TweetReader.TweetInputInfo input = (TweetReader.TweetInputInfo) o;
        TweetProcessor.TweetProcessorInfo info = new TweetProcessorInfo(input.user, input.tweet);

        // Find mentions.
        Matcher mentionMatcher = KwetterService.REGEX_MENTION.matcher(info.tweet);
        while (mentionMatcher.find()) info.mentions.add(mentionMatcher.group("username"));

        // Find hashtags.
        Matcher hashtagMatcher = KwetterService.REGEX_HASHTAG.matcher(info.tweet);
        while (hashtagMatcher.find()) info.hashtags.add(hashtagMatcher.group("hashtag"));

        return info;
    }
}
