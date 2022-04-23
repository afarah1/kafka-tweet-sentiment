package kafkatweets.serdes.json;

import kafkatweets.serdes.Tweet;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Custom deserializer for tweets.
 */
public class TweetDeserializer implements Deserializer<Tweet> {

  private Gson gson = new Gson();

  /**
   * Deserializes a Tweet from a JSON encoded as a bytestream.
   *
   * @see https://github.com/apache/kafka/blob/1.0/clients/src/main/java/org/apache/kafka/common/serialization/Deserializer.java#L46
   */
  @Override
  public Tweet 
  deserialize(String topic, byte[] bytes) 
  {
    // String(bytes ...) throws NullPointerException if bytes is empty.
    // Empty bytes is a perfectly valid input, so just return null.
    if (bytes == null)
      return null;

    // Throws JsonSyntaxException (unchecked) 
    return gson.fromJson(
      new String(bytes, StandardCharsets.UTF_8),
      Tweet.class
    );
  }

}
