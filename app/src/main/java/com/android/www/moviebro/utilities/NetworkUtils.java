package com.android.www.moviebro.utilities;

import android.net.Uri;
import android.util.Log;

import com.android.www.moviebro.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_KEY_QUERY_PARAM = "api_key";
    private static final String API_KEY_VALUE = BuildConfig.MOVIE_API_KEY;

    private static final String VIDEO_URL_PATH = "videos";
    private static final String REVIEW_URL_PATH = "reviews";

    public static URL buildUrl(String requestUrlPath) {

        Uri baseUri = Uri.parse(MOVIE_DB_BASE_URL);

        Uri.Builder builder = baseUri.buildUpon();

        builder.appendPath(requestUrlPath);
        builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY_VALUE);


        Uri completeUri = builder.build();

        URL requestUrl;

        try {
            requestUrl = new URL(completeUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return requestUrl;
    }

    public static URL buildVideoUrl(int videoId) {
        Uri baseUri = Uri.parse(MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath(String.valueOf(videoId))
                .appendPath(VIDEO_URL_PATH)
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY_VALUE)
                .build();

        URL requestUrl;
        try {
            requestUrl = new URL(baseUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return requestUrl;
    }

    public static URL buildReviewUrl(int videoId) {
        Uri baseUri = Uri.parse(MOVIE_DB_BASE_URL)
                .buildUpon()
                .appendPath(String.valueOf(videoId))
                .appendPath(REVIEW_URL_PATH)
                .appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY_VALUE)
                .build();

        URL requestUrl;
        try {
            requestUrl = new URL(baseUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return requestUrl;
    }

    public static String buildMoviePosterUrl(String imagePath, String posterSize) {

        final String imageBaseUrl = "https://image.tmdb.org/t/p/";

        Uri baseUri = Uri.parse(imageBaseUrl);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendEncodedPath(posterSize);
        builder.appendEncodedPath(imagePath);

        return builder.build().toString();
    }

    public static String getJsonResponseFromHttpUrl(URL requestUrl) {
        if (requestUrl == null) {
            return null;
        }
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                inputStream = httpURLConnection.getInputStream();

                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    return scanner.next();
                } else {
                    return null;
                }
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }
    }
}
