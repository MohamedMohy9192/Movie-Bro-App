package com.android.www.moviebro.utilities;

import android.text.TextUtils;

import com.android.www.moviebro.model.Movie;
import com.android.www.moviebro.model.MovieReview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class JsonUtils {

    private static final String MOVIE_ARRAY_KEY = "results";

    public static final String ID = "id";
    public static final String TITLE = "original_title";
    public static final String POSTER = "poster_path";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATE = "release_date";

    public static final String VIDEO_ARRAY_KEY = "results";
    public static final String VIDEO_ID_KEY = "key";

    public static final String REVIEW_ARRAY_KEY = "results";
    public static final String REVIEW_AUTHOR_KEY = "author";
    private static final String REVIEW_CONTENT_KEY = "content";

    public static List<Movie> extractMovieFromJson(String jsonResponse) {

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Movie> movieList = new ArrayList<>();

        int id = 0;
        String title = null;
        String poster = null;
        String overview = null;
        String voteAverage = null;
        String releaseDate = null;

        try {
            JSONObject mainObject = new JSONObject(jsonResponse);

            if (mainObject.has(MOVIE_ARRAY_KEY)) {
                JSONArray movieArray = mainObject.optJSONArray(MOVIE_ARRAY_KEY);

                int movieArrayLength = movieArray.length();
                for (int i = 0; i < movieArrayLength; i++) {
                    JSONObject movieObject = movieArray.optJSONObject(i);

                    if (movieObject.has(ID)) {
                        id = movieObject.optInt(ID);
                    }

                    if (movieObject.has(TITLE)) {
                        title = movieObject.optString(TITLE);
                    }

                    if (movieObject.has(POSTER)) {
                        poster = movieObject.optString(POSTER);
                    }

                    if (movieObject.has(OVERVIEW)) {
                        overview = movieObject.optString(OVERVIEW);
                    }

                    if (movieObject.has(VOTE_AVERAGE)) {
                        voteAverage = movieObject.optString(VOTE_AVERAGE);
                    }

                    if (movieObject.has(RELEASE_DATE)) {
                        releaseDate = movieObject.optString(RELEASE_DATE);
                    }

                    Movie movie = new Movie(id, title, poster, overview, voteAverage, releaseDate);
                    movieList.add(movie);
                }
            }
            return movieList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> extractVideosIds(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<String> videoList = new ArrayList<>();

        try {
            JSONObject videoObject = new JSONObject(jsonResponse);

            JSONArray videoResultsArray = videoObject.optJSONArray(VIDEO_ARRAY_KEY);

            int videoArrayLength = videoResultsArray.length();
            for (int i = 0; i < videoArrayLength; i++) {
                JSONObject videoIdObject = videoResultsArray.getJSONObject(i);

                if (videoIdObject.has(VIDEO_ID_KEY)) {
                    String videoId = videoIdObject.optString(VIDEO_ID_KEY);
                    videoList.add(videoId);
                }
            }
            return videoList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<MovieReview> extractMovieReviews(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<MovieReview> movieReviews = new ArrayList<>();

        String authorName = null;
        String reviewContent = null;

        try {
            JSONObject reviewObject = new JSONObject(jsonResponse);

            JSONArray reviewResultArray = reviewObject.optJSONArray(REVIEW_ARRAY_KEY);

            int reviewArrayLength = reviewResultArray.length();
            for (int i = 0; i < reviewArrayLength; i++) {
                JSONObject reviewContentObject = reviewResultArray.optJSONObject(i);

                if (reviewContentObject.has(REVIEW_AUTHOR_KEY)) {
                    authorName = reviewContentObject.optString(REVIEW_AUTHOR_KEY);
                }
                if (reviewContentObject.has(REVIEW_CONTENT_KEY)) {
                    reviewContent = reviewContentObject.optString(REVIEW_CONTENT_KEY);
                }

                MovieReview movieReview = new MovieReview(authorName, reviewContent);
                movieReviews.add(movieReview);
            }
            return movieReviews;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}