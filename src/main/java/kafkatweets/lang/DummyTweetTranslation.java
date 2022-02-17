package kafkatweets.lang;

import kafkatweets.serdes.Tweet;

public class DummyTweetTranslation implements TweetTranslationInterface {
  @Override
  public Tweet translate(Tweet tweet, String targetLang) {
    tweet.text = "DUMMY_TRANSLATE: " + tweet.text;
    return tweet;
  }
}
