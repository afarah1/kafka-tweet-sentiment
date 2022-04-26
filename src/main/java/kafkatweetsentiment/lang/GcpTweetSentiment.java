package kafkatweetsentiment.lang;

import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;
import kafkatweetsentiment.avro.EntitySentiment;
import kafkatweetsentiment.serdes.Tweet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GcpTweetSentiment implements TweetSentimentInterface {
  /*
   * NLP client - this has to be thread-local, see:
   * https://forum.confluent.io/t/question-regarding-thread-local-variables-within-a-stream-processing-topology/4870
   */
  private static ThreadLocal<LanguageServiceClient> nlpClients =
      ThreadLocal.withInitial(
          () -> {
            try {
              LanguageServiceSettings settings = LanguageServiceSettings.newBuilder().build();
              LanguageServiceClient client = LanguageServiceClient.create(settings);
              return client;
            /* See https://stackoverflow.com/a/22000937/7050476 */
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });

  @Override
  public List<EntitySentiment> 
  getEntitiesSentiment(Tweet tweet) 
  {
    /* 
     * Build the request to GCP's NLP API
     */
    Document doc = Document.newBuilder()
      .setContent(tweet.text)
      .setType(Type.PLAIN_TEXT)
      .build();
    AnalyzeEntitySentimentRequest request = AnalyzeEntitySentimentRequest.newBuilder()
      .setDocument(doc)
      .setEncodingType(EncodingType.UTF8)
      .build();
    /*
     * Request the analysis and obtain the response
     */
    AnalyzeEntitySentimentResponse response = nlpClients.get().analyzeEntitySentiment(request);
    /*
     * For every entity in the response, create an EntitySentiment record
     */
    List<EntitySentiment> ans = new ArrayList<>();
    for (Entity entity : response.getEntitiesList()) {
      Sentiment sentiment = entity.getSentiment();
      EntitySentiment entitySentiment = EntitySentiment.newBuilder()
        .setCreatedAt(tweet.createdAt)
        .setId(tweet.id)
        .setEntity(entity.getName().replace("#", "").toLowerCase())
        .setText(tweet.text)
        .setSalience(entity.getSalience())
        .setSentimentScore(sentiment.getScore())
        .setSentimentMagnitude(sentiment.getMagnitude())
        .build();
      ans.add(entitySentiment);
    }
    return ans;
  }

}
