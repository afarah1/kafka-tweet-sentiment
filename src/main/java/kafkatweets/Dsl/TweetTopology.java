package kafkatweets.Dsl;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.common.serialization.Serdes;
import kafkatweets.serdes.Tweet;
import kafkatweets.serdes.json.TweetSerdes;

class TweetTopology {

  private static final String STREAM_NAME = "tweets";

  public static Topology build() {
    StreamsBuilder builder = new StreamsBuilder();

    KStream<byte[], Tweet> stream = builder.stream(
      STREAM_NAME,
      Consumed.with(Serdes.ByteArray(), new TweetSerdes())
    );

    stream.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweets-stream"));

    return builder.build();
  }

}
