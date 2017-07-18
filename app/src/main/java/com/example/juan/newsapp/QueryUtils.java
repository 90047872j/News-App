package com.example.juan.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_SECTION_NAME = "sectionName";
    private static final String KEY_WEB_PUBLICATION_DATE = "webPublicationDate";
    private static final String KEY_WEB_TITLE = "webTitle";
    private static final String KEY_FIELDS = "fields";
    private static final String KEY_TRAIL_TEXT = "trailText";
    private static final String KEY_BY_LINE = "byline";
    private static final String KEY_SHORTURL = "shortUrl";

    private QueryUtils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    public static List<News> fetchNewsData(String requestUrl) {
        Log.e("Request URL", requestUrl);
        URL url = createUrl(requestUrl);
        String response = null;
        try {
            response = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<News> news = extractFeatureFromJson(response);
        return news;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String response = "";
        if (url == null) {
            return response;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return response;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        List<News> newsList = new ArrayList<>();
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        try {
            String sectionName = "Section not available";
            String webPublicationDate = "Date not available";
            String webTitle = null;
            String trailText = "Description not available";
            String byline = "Author not available";
            String shortUrl = null;
            JSONArray newsArray;
            JSONObject news;
            JSONObject currentNews;
            JSONObject fields;
            JSONObject baseJson = new JSONObject(newsJSON);
            if (!baseJson.has(KEY_RESPONSE)) {
                return null;
            } else {
                news = baseJson.getJSONObject(KEY_RESPONSE);
                if (!news.has(KEY_RESULTS)) {
                    return null;
                } else {
                    newsArray = news.getJSONArray(KEY_RESULTS);
                }
                for (int i = 0; i < newsArray.length(); i++) {
                    currentNews = newsArray.getJSONObject(i);
                    if (currentNews.has(KEY_SECTION_NAME)) {
                        sectionName = currentNews.getString(KEY_SECTION_NAME);
                    }
                    if (currentNews.has(KEY_WEB_PUBLICATION_DATE)) {
                        webPublicationDate = currentNews.getString(KEY_WEB_PUBLICATION_DATE);
                    }
                    if (currentNews.has(KEY_WEB_TITLE)) {
                        webTitle = currentNews.getString(KEY_WEB_TITLE);
                    }
                    if (currentNews.has(KEY_FIELDS)) {
                        fields = currentNews.getJSONObject(KEY_FIELDS);
                        if (fields.has(KEY_TRAIL_TEXT)) {
                            trailText = fields.getString(KEY_TRAIL_TEXT);
                        }
                        if (fields.has(KEY_BY_LINE)) {
                            byline = fields.optString(KEY_BY_LINE);
                        }
                        if (fields.has(KEY_SHORTURL)) {
                            shortUrl = fields.getString(KEY_SHORTURL);
                        }
                    }
                    News newsArticle = new News(sectionName, webPublicationDate, webTitle, trailText, byline, shortUrl);
                    newsList.add(newsArticle);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON results", e);
        }
        return newsList;
    }
}
