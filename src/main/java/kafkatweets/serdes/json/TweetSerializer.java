package kafkatweets.serdes.json;

import kafkatweets.serdes.Tweet;
import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Serializer;

public class TweetSerializer implements Serializer<Tweet> {

  private Gson gson = new Gson();

  @Override
  public byte[] serialize(String topic, Tweet tweet) {
    // Empty tweet is a perfectly valid input, so just return null.
    if (tweet == null)
      return null;

    return gson.toJson(tweet).getBytes(StandardCharsets.UTF_8);
  }

}
