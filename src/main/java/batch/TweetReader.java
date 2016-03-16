package batch;

import com.google.gson.Gson;

import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.io.*;
import java.util.*;

public class TweetReader implements ItemReader {
    public class TweetInputInfo {
        public final String user;
        public final String tweet;

        public TweetInputInfo(String user, String tweet) {
            this.user = user;
            this.tweet = tweet;
        }
    }

    @Inject private JobContext jobContext;

    private int current = 0;
    private List<Map<String, Object>> tweets;

    @Override
    public void open(Serializable serializable) throws Exception {
        // Get the filename
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Properties jobParameters = jobOperator.getParameters(jobContext.getExecutionId());
        String resourceName = (String) jobParameters.get("tweetInputDataFileName");

        // Read the file.
        Gson gson = new Gson();
        Map<String, Object> data = gson.<HashMap<String, Object>>fromJson(new FileReader(resourceName), HashMap.class);
        tweets = (List<Map<String, Object>>) data.getOrDefault("Tweets", new ArrayList<HashMap<String, Object>>());

        // Restore position.
        if (serializable != null) {
            current = (Integer) serializable;
        }
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Object readItem() throws Exception {
        Map<String, Object> tweet = tweets.get(current);
        TweetInputInfo info = new TweetInputInfo(tweet.get("screenName").toString(), tweet.get("tweet").toString());
        current++;
        return info;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return current;
    }
}
