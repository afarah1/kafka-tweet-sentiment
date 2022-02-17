package kafkatweets.serdes;

import com.google.gson.annotations.SerializedName;

public class Tweet {
  @SerializedName("CreatedAt")
  public Long createdAt;

  @SerializedName("Id")
  public Long id;

  @SerializedName("Lang")
  public String lang;

  @SerializedName("Text")
  public String text;

  //@SerializedName("Retweet")
  //private Boolean retweet;

  @SerializedName("FollowersCount")
  public Long followersCount;
}
