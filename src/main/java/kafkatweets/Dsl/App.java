package kafkatweets.Dsl;

import java.util.Properties;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

class App {

  public static void main(String[] args) {

    Topology topology = TweetTopology.build();

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafkatweets");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
    config.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);

    KafkaStreams streams = new KafkaStreams(topology, config);

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    System.out.println("Starting");
    streams.start();
  }

}
