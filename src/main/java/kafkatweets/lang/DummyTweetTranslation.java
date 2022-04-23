package kafkatweets.lang;

import kafkatweets.serdes.Tweet;

/**
 * Dummy for testing purposes.
 */
public class DummyTweetTranslation implements TweetTranslationInterface {
  @Override
  public Tweet 
  translate(Tweet tweet, String targetLang) 
  {
    tweet.text = "DUMMY_TRANSLATE: " + tweet.text;
    return tweet;
  }
}
