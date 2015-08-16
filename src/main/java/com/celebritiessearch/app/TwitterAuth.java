package com.celebritiessearch.app;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by akhmedovi on 8/15/15.
 * email: ssymbol.1993@gmail.com
 * Twitter Celebrities Search Sample Project
 */
public class TwitterAuth {

    private static final Random RAND = new Random();

    /**
     *
     * @param twitterClient
     * @param method
     * @param url
     * @param formData
     * @return Authorization header value (e.g. OAuth oauth_consumer_key="xvz1evFS4wEEPTGEFPHBog",
                                            oauth_nonce="kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
                                            oauth_signature="tnnArxj06cWHq44gCs1OSKk%2FjLY%3D",
                                            oauth_signature_method="HMAC-SHA1",
                                            oauth_timestamp="1318622958",
                                            oauth_token="370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb",
                                            oauth_version="1.0")
     * @throws IllegalArgumentException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    protected static String getAuthorizationHeader(TwitterClient twitterClient, String method, URL url, HashMap<String, String> formData) throws IllegalArgumentException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = timestamp + Math.abs(RAND.nextInt());

        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("oauth_consumer_key", twitterClient.getConsumerKey());
        params.put("oauth_nonce", nonce);
        params.put("oauth_signature_method", "HMAC-SHA1");
        params.put("oauth_timestamp", timestamp);
        params.put("oauth_token", twitterClient.getAccessToken());
        params.put("oauth_version", "1.0");
        params.put("oauth_signature", generateSignature(twitterClient, method, url, params, formData));

        params = sortEncodeMap(params);

        StringBuilder authHeader = new StringBuilder("OAuth ");
        boolean isFirst = true;
        for (String key : params.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                authHeader.append(", ");
            }

            authHeader.append(key);
            authHeader.append("=");
            authHeader.append("\"");
            authHeader.append(params.get(key));
            authHeader.append("\"");
        }

        return authHeader.toString();
    }

    /**
     * Twitter requires signature to be generated with sorted encoded keys
     * @param src
     * @return sorted TreeMap (sorts by percent-encoded key)
     */
    protected static TreeMap<String, String> sortEncodeMap(Map<String, String> src) {
        TreeMap<String, String> dst = new TreeMap<String, String>();

        for (String k : src.keySet()) {
            dst.put(TwitterEncoder.percentEncode(k), TwitterEncoder.percentEncode(src.get(k)));
        }

        return dst;
    }


    /**
     * Generate signature of OAuth 1.0 in copliance with Twitter API 1.1
     * @param twitterClient
     * @param method
     * @param url
     * @param authParams
     * @param params
     * @return oauth_signature param for Twitter
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    protected static String generateSignature(TwitterClient twitterClient, String method, URL url, Map<String, String> authParams,
                                            Map<String, String> params) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        HashMap<String, String> signaturePartials = new HashMap<String, String>();
        signaturePartials.putAll(authParams);
        signaturePartials.putAll(params);
        signaturePartials.putAll(extractUrlParams(url));

        TreeMap<String, String> authSignatureData = sortEncodeMap(signaturePartials);

        StringBuilder paramsSignature = new StringBuilder();
        boolean isFirst = true;
        for (String key : authSignatureData.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                paramsSignature.append("&");
            }

            paramsSignature.append(key);
            paramsSignature.append("=");
            paramsSignature.append(authSignatureData.get(key));
        }

        String signatureKey = TwitterEncoder.percentEncode(twitterClient.getConsumerSecret()) + "&" +
                TwitterEncoder.percentEncode(twitterClient.getAccessTokenSecret());

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec(signatureKey.getBytes(), "HmacSHA1"));

        String signatureVal = method + "&" +
                TwitterEncoder.percentEncode(extractUrlBase(url)) + "&" +
                TwitterEncoder.percentEncode(paramsSignature.toString());

        byte[] byteHMAC = mac.doFinal(signatureVal.getBytes());

        return DatatypeConverter.printBase64Binary(byteHMAC);
    }

    /**
     *
     * @param url
     * @return map of params of query (e.g. p1=v1&p2=v2 as {p1 =>v1, p2 => v2})
     * @throws UnsupportedEncodingException
     */
    protected static Map<String, String> extractUrlParams(URL url) throws UnsupportedEncodingException {
        HashMap<String, String> queryParams = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] data = pair.split("=");

            if (data.length != 2) {
                throw new IllegalArgumentException("URL is not properly formatted");
            }

            queryParams.put(URLDecoder.decode(data[0], "UTF-8"), URLDecoder.decode(data[1], "UTF-8"));
        }

        return queryParams;
    }

    /**
     * @param u - URL object
     * @return base url without ?query and #ci_params
     */
    protected static String extractUrlBase(URL u) {

        String url = u.toString();

        if (url != null) {
            int queryPosition = url.indexOf("?");
            if (queryPosition <= 0) {
                queryPosition = url.indexOf("#");
            }

            if (queryPosition >= 0) {
                url = url.substring(0, queryPosition);
            }
        }
        return url;
    }

}
