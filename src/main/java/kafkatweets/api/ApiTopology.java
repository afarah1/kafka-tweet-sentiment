package kafkatweets.api;

import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.KafkaStreams;

class APITopology {

  private static final String SOURCE_NAME = "TweetsSource";
  private static final String STREAM_NAME = "tweets";
  private static final String PROCESSOR_NAME = "PrintTweet";

  public static void main(String[] args) {

    //
    // Create the topology
    //
    Topology topology = new Topology();
    topology.addSource(SOURCE_NAME, STREAM_NAME);
    topology.addProcessor(PROCESSOR_NAME, PrintTweetProcessor::new, SOURCE_NAME);

    // 
    // Streams and Consumer configuration
    //
    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkatweets");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
    // Consume records from the beginning if no offset is present
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); 
    // See
    // https://kafka.apache.org/10/documentation/streams/developer-guide/datatypes
    //
    //config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Void().getClass());
    //config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    //
    // Start
    //
    KafkaStreams streams = new KafkaStreams(topology, config);
    System.out.println("Starting streams");
    streams.start();

    //
    // Close Kafka Streams when the JVM shuts down (e.g. SIGTERM)
    //
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }
}
