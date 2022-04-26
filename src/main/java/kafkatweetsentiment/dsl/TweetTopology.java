package kafkatweetsentiment.dsl;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Named;
import kafkatweetsentiment.serdes.Tweet;
import kafkatweetsentiment.serdes.json.TweetSerdes;
import kafkatweetsentiment.lang.TweetSentimentInterface;
import kafkatweetsentiment.lang.TweetTranslationInterface;
import kafkatweetsentiment.avro.EntitySentiment;

/**
 * The stream processing topology for Tweets.
 */
class TweetTopology {

  /**
   * The name of the stream from which we will be reading.
   */
  private static final String 
  STREAM_NAME = "tweets";

  /**
   * Builds a new topology with the received translator.
   *
   * @param translationClient Object that translates tweets from other languages to English
   * @param sentimentClient Object that performns sentiment analysis on English tweets
   * @param entities Entities to perform sentiment analysis on (will be all tweet words if empty)
   */
  public static Topology 
  build(TweetTranslationInterface translationClient, TweetSentimentInterface sentimentClient, List<String> entities) 
  {
    /*
     * Build the stream using the DSL. It will read from STREAM_NAME and use
     * our custom TweetSerdes.
     */
    StreamsBuilder builder = new StreamsBuilder();
    KStream<byte[], Tweet> stream = builder.stream(
      STREAM_NAME,
      Consumed.with(Serdes.ByteArray(), new TweetSerdes())
    );

    // If we wish to filter we would use this
    // KStream<byte[], Tweet> filtered = stream.filter(
    //   (key, tweet) -> {
    //     return tweet.lang.equals("en");
    //   }
    // );

    /*
     * Create predicates (filter that returns a boolean) for branching English
     * and non-English tweets.
     */
    Predicate<byte[], Tweet> englishTweets = 
      (key, tweet) -> tweet.lang.equals("en");
    Predicate<byte[], Tweet> nonEnglishTweets = 
      (key, tweet) -> !tweet.lang.equals("en");

    /*
     * Actually split the stream
     *
     * Use split() - branch() has been deprecated on 2.8, it lead to warnings
     * on 2.7. 
     *
     * For more info on the deprecation/warnings, see:
     *
     * https://issues.apache.org/jira/browse/KAFKA-8296
     * https://stackoverflow.com/questions/21132692/java-unchecked-unchecked-generic-array-creation-for-varargs-parameter
     *
     * For more info on the usage of split(), see:
     * 
     * https://kafka.apache.org/28/javadoc/org/apache/kafka/streams/kstream/BranchedKStream.html
     */
    Map<String, KStream<byte[], Tweet>> branched = stream.split(Named.as("lang-"))
      .branch(englishTweets, Branched.as("en"))
      .branch(nonEnglishTweets, Branched.as("int"))
      .defaultBranch();

    /* 
     * Translate the non-English tweets into a new Translated stream 
     */
    KStream<byte[], Tweet> nonEnglishStream = branched.get("lang-int");
    KStream<byte[], Tweet> translatedStream = nonEnglishStream.mapValues(
      (tweet) -> { return translationClient.translate(tweet, "en"); }
    );

    /* 
     * Merge the English and Translated streams 
     */
    KStream<byte[], Tweet> merged = branched.get("lang-en").merge(translatedStream);

    /*
     * Create a new Enriched stream of sentiment analysed tweets (EntitySentiment)
     */
    KStream<byte[], EntitySentiment> enriched = merged.flatMapValues(
      (tweet) -> {
        /* Get the sentiment analysed record */
        List<EntitySentiment> results = sentimentClient.getEntitiesSentiment(tweet);
        /* Lambda expression to filter out unwanted currencies */
        results.removeIf(
          entitySentiment -> !entities.contains(entitySentiment.getEntity())
        );
        return results;
      }
    );

    // TODO write to a sink
    //merged.print(Printed.<byte[], Tweet>toSysOut().withLabel("merged-stream"));
    enriched.print(Printed.<byte[], EntitySentiment>toSysOut().withLabel("enriched-stream"));

    return builder.build();
  }

}
