package kafkatweets.serdes.json;

import kafkatweets.serdes.Tweet;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Deserializer;

public class TweetDeserializer implements Deserializer<Tweet> {

  private Gson gson = new Gson();

  @Override
  public Tweet deserialize(String topic, byte[] bytes) {
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
