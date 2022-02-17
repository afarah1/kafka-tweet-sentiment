package kafkatweets.lang;

import kafkatweets.serdes.Tweet;

/**
 * Tweet translation interface to be used with Kafka Streams operations such as
 * mapValue.
 */
public interface TweetTranslationInterface {
  public Tweet translate(Tweet tweet, String targetLang);
}
