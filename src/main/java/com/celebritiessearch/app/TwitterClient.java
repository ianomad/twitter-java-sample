package com.celebritiessearch.app;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by akhmedovi on 8/15/15.
 * email: ssymbol.1993@gmail.com
 * Twitter Celebrities Search Sample Project
 */
public class TwitterClient {

    private static final String TWEET_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json";

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    public TwitterClient(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    /**
     * @param celebrityName
     * @param location
     * @return most popular tweets (as list of texts) containing celebrityName and location (15 by default)
     * @throws Exception
     */
    public ArrayList<String> getLatestTweets(String celebrityName, String location) throws Exception {
        if (null == celebrityName || celebrityName.trim().isEmpty()) {
            throw new IllegalArgumentException("Celebrity name can't be empty");
        }

        if (null == location || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location name can't be empty");
        }

        String queryParam = TwitterEncoder.percentEncode("" + celebrityName + " " + location);

        URL url = new URL(TWEET_SEARCH_URL + "?q=" + queryParam);
        String response = runAuthorizedRequest(url, "GET");

        Gson gson = new Gson();
        TwitterResponse twitterResponse = gson.fromJson(response, TwitterResponse.class);
        if (null == twitterResponse.statuses) {
            return null;
        }

        ArrayList<String> res = new ArrayList<String>();
        for (TwitterResponse.TwitterStatus status : twitterResponse.statuses) {
            res.add(status.text);
        }

        return res;
    }

    /**
     * Adds header Authorization (OAuth 1.0)
     * @param url
     * @param method
     * @return result from http request as String
     * @throws Exception
     */
    private String runAuthorizedRequest(URL url, String method) throws Exception {

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        //Twitter Auth 1.0
        con.setRequestProperty("Authorization", TwitterAuth.getAuthorizationHeader(this, method, url,
                new HashMap<String, String>()));

        return parseResponse(con);
    }

    /**
     * Stream to String
     * @param con
     * @return Http response as String
     * @throws Exception
     */
    private String parseResponse(HttpURLConnection con) throws Exception {
        int responseCode = con.getResponseCode();

        InputStream is;
        boolean isSuccess = true;
        if (responseCode != 200) {
            is = con.getErrorStream();
            isSuccess = false;
        } else {
            is = con.getInputStream();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if (!isSuccess) {
            throw new Exception("Twitter Error: " + response.toString());
        }

        return response.toString();
    }
}
