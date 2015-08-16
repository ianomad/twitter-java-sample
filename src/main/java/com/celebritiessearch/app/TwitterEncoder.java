package com.celebritiessearch.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by akhmedovi on 8/15/15.
 * Encodes in compliance with Twitter Percentage Encode rules
 * Twitter encoding (https://dev.twitter.com/oauth/overview/percent-encoding-parameters)
 */
public class TwitterEncoder {

    /**
     * Special Twitter Encoding for Auth 1.0
     * @param s
     * @return
     * @throws RuntimeException
     */
    public static String percentEncode(String s) throws RuntimeException {
        if (s == null) {
            return "";
        }
        try {
            String regEncoded = URLEncoder.encode(s, "UTF-8");

            return regEncoded.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }
}
