package kafkatweetsentiment.lang;

import kafkatweetsentiment.serdes.Tweet;

/**
 * Tweet translation interface to be used with Kafka Streams operations such as
 * mapValue.
 */
public interface TweetTranslationInterface {
  /**
   * Returns a new Tweet, translating the original to the target language.
   *
   * @param tweet The original tweet to be translated
   * @param targetLang The target language
   */
  public Tweet 
  translate(Tweet tweet, String targetLang);
}
