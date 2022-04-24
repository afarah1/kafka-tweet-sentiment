package kafkatweets.lang;

// import java.lang.Iterable;
import java.util.List;
import java.util.ArrayList;
import com.google.common.base.Splitter;
import kafkatweets.serdes.Tweet;
import kafkatweets.avro.EntitySentiment;

/**
 * Dummy for testing purposes.
 */
public class DummyTweetSentiment implements TweetSentimentInterface {
  @Override
  public List<EntitySentiment>
  getEntitiesSentiment(Tweet tweet)
  {
    // See https://stackoverflow.com/a/858590/7050476
    List<EntitySentiment> ans = new ArrayList<>();  

    // See:
    // https://www.baeldung.com/java-split-string
    // https://en.wikipedia.org/wiki/Fluent_interface
    Iterable<String> words = Splitter.on(' ').split(tweet.text.toLowerCase().replace("#", ""));
    for (String entity : words) {
      EntitySentiment entitySentiment = EntitySentiment.newBuilder()
        .setCreatedAt(tweet.createdAt)
        .setId(tweet.id)
        .setEntity(entity)
        .setText(tweet.text)
        .setSalience(0.5f) // Dummy values
        .setSentimentScore(0.5f)
        .setSentimentMagnitude(0.5f)
        .build();
      ans.add(entitySentiment);
    }
    return ans;
  }
}
