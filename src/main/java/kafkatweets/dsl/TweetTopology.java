package kafkatweets.dsl;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.common.serialization.Serdes;
import kafkatweets.serdes.Tweet;
import kafkatweets.serdes.json.TweetSerdes;
import kafkatweets.lang.TweetTranslationInterface;
import kafkatweets.lang.DummyTweetTranslation;

class TweetTopology {

  private static final String STREAM_NAME = "tweets";

  public static Topology build() {
    // TODO implement translation
    return build(new DummyTweetTranslation());
  }

  public static Topology build(TweetTranslationInterface tweetTranslation) {
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

    Predicate<byte[], Tweet> englishTweets = 
      (key, tweet) -> tweet.lang.equals("en");

    Predicate<byte[], Tweet> nonEnglishTweets = 
      (key, tweet) -> !tweet.lang.equals("en");

    KStream<byte[], Tweet>[] branches = stream.branch(englishTweets, nonEnglishTweets);
    KStream<byte[], Tweet> englishStream = branches[0];
    KStream<byte[], Tweet> nonEnglishStream = branches[1];

    KStream<byte[], Tweet> translatedStream = nonEnglishStream.mapValues(
      (tweet) -> { return tweetTranslation.translate(tweet, "en"); }
    );

    KStream<byte[], Tweet> merged = englishStream.merge(translatedStream);

    merged.print(Printed.<byte[], Tweet>toSysOut().withLabel("tweets-stream"));

    return builder.build();
  }

}
