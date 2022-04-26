package kafkatweetsentiment.serdes.json;

import kafkatweetsentiment.serdes.Tweet;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

/**
 * Custom SerializerDeserializer for a Tweet.
 */
public class TweetSerdes implements Serde<Tweet> {

  /**
   * Constructs a new TweetSerializer.
   */
  @Override
  public Serializer<Tweet> 
  serializer() 
  {
    return new TweetSerializer();
  }

  /**
   * Constructs a new TweetDeserializer.
   */
   @Override
  public Deserializer<Tweet> 
  deserializer() 
  {
    return new TweetDeserializer();
  }
}

