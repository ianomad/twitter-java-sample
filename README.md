# Twitter API 1.1 sample project

Sample project for Twitter API 1.1 OAuth 1.0

For testing run tests
Or simply run 'mvn install'

For usage:

```java
String name = "Tom Cruise";
String location = "Los Angeles";

ArrayList<String> latestTweets = new TwitterClient(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
        .getLatestTweets(name, location);

for (String latestTweet : latestTweets) {
    System.out.println(latestTweet);
}
```