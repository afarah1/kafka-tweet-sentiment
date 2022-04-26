package kafkatweetsentiment.dsl;

import java.lang.System;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import com.google.common.base.Splitter;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import kafkatweetsentiment.lang.GcpTweetSentiment;
import kafkatweetsentiment.lang.GcpTweetTranslation;

/**
 * Performs sentiment analysis on tweets, translating to English as necessary.
 */
class App {

  /**
   * Returns the entities list for this environment, empty if not present.
   *
   * We use an environment variable because the same list might be used by
   * other applications such as the source connector.
   */
  private static List<String>
  getEntities()
  {
    List<String> ans;
    try {
      ans = Splitter.on(',').splitToList(System.getenv("SENTIMENT_ENTITIES_LIST"));
    } catch (Exception e) {
      // Empty entities list is a valid return value
      e.printStackTrace(System.err);
      ans = new ArrayList<String>();
    }
    return ans;
  }

  /**
   * Return the broker endpoint for this environment, default localhost:9092.
   */
  private static String
  getBrokerEndpoint()
  {
    String ans = null;
    try {
      ans = System.getenv("SENTIMENT_BROKER_ENDPOINT");
    } catch (Exception e) {
      // Empty endpoint is a valid return value
      e.printStackTrace(System.err);
    }
    if (ans == null)
      ans = "localhost:9092";
    return ans;
 
  }

  public static void 
  main(String[] args) 
  {   
    /*
     * Build the topology using GCP client
     * See lang/Dummy* if you do not want to use GCP
     */
    Topology topology = TweetTopology.build(
      new GcpTweetTranslation(), 
      new GcpTweetSentiment(),
      getEntities()
    );

    /*
     * Set Streams properties
     */
    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkatweetsentiment");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokerEndpoint());
    properties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
 
    /*
     * Start the consumer
     */
    KafkaStreams streams = new KafkaStreams(topology, properties);
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    System.out.println("App starting");
    streams.start();
  }

}
