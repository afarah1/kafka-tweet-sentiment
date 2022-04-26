package kafkatweetsentiment.lang;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import kafkatweetsentiment.serdes.Tweet;

public class GcpTweetTranslation implements TweetTranslationInterface {

  @Override
  public Tweet 
  translate(Tweet tweet, String targetLanguage) 
  {
    Translate translationClient = TranslateOptions.getDefaultInstance().getService();
    Translation translation = translationClient.translate(
      tweet.text,
      TranslateOption.sourceLanguage(tweet.lang),
      TranslateOption.targetLanguage(targetLanguage)
    );
    tweet.text = translation.getTranslatedText();
    return tweet;
  }

}
