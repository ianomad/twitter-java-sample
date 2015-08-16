package com.celebritiessearch.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    private static final String CONSUMER_KEY = "MRNM7Xbz0gu71QL1FWC3xvSye";
    private static final String CONSUMER_SECRET = "uO6QqbvtYVrfT9IgzbBWAYtw6Sckwi7x9GCnOqOSxMB65s6wjq";
    private static final String ACCESS_TOKEN = "244429337-RDuVmDm8Dk38XZ9ddyQ8TPjb3ySWCuOEG6VXyf39";
    private static final String ACCESS_TOKEN_SECRET = "4esMRSVxxs8Vu2wTfCHLubiS1PTAAQJOaTzwTmjgJ36W4";


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    public void testTweets() throws Exception {

        String name = "Tom Cruise";
        String location = "Los Angeles";

        ArrayList<String> latestTweets = new TwitterClient(CONSUMER_KEY, CONSUMER_SECRET, ACCESS_TOKEN, ACCESS_TOKEN_SECRET)
                .getLatestTweets(name, location);

        int c = 1;
        for (String latestTweet : latestTweets) {
            System.out.print(c + ": ");
            System.out.println(latestTweet);
            c++;
        }
    }
}
