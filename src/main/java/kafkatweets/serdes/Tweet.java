package kafkatweets.serdes;

import com.google.gson.annotations.SerializedName;

public class Tweet {
  @SerializedName("CreatedAt")
  private Long createdAt;

  @SerializedName("Id")
  private Long id;

  @SerializedName("Lang")
  private String lang;

  @SerializedName("Text")
  private String text;

  //@SerializedName("Retweet")
  //private Boolean retweet;

  @SerializedName("FollowersCount")
  private Long followersCount;
}
