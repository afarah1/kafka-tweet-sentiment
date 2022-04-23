package kafkatweets.lang;

import java.util.List;
import kafkatweets.serdes.Tweet;
import kafkatweets.avro.EntitySentiment;

/**
 * Tweet sentiment analysis interface to be used with Kafka Streams operations
 * such as flatMapValues.
 */
public interface TweetSentimentInterface {
  /**
   * Performns sentiment analysis on a Tweet.
   *
   * Example: #bitcoin is looking super strong. #ethereum has me worried though
   * Returns: [{ "entity": "bitcoin", "sentiment_score": 0.80 },
   *           { "entity": "ethereum", "sentiment_score": -0.20 }]
   *
   * @param tweet The tweet to be analysed
   * @return A list of EntitySentiment with the sentiment for each entity
   */
  public List<EntitySentiment>
  getEntitiesSentiment(Tweet tweet);
}
