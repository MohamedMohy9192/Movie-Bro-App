package com.android.www.moviebro;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.www.moviebro.data.MovieContract.FavoriteEntry;
import com.android.www.moviebro.model.MovieReview;
import com.android.www.moviebro.utilities.JsonUtils;
import com.android.www.moviebro.utilities.NetworkUtils;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @BindView(R.id.video_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.review_recycler_view)
    RecyclerView mReviewsRecyclerView;

    private VideoAdapter mVideoAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private int mMovieId;

    private final static int VIDEO_LOADER_ID = 1;

    private final static int REVIEW_LOADER_ID = 2;

    private ContentValues contentValues = new ContentValues();

    private int isFavorite;

    @BindView(R.id.fab_fav)
    FloatingActionButton mActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        final YouTubeInitializationResult result =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (result != YouTubeInitializationResult.SUCCESS) {
            result.getErrorDialog(this, 0).show();
        }

        Intent intent = getIntent();
        getMovieData(intent);


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mVideoAdapter = new VideoAdapter(this);

        mRecyclerView.setAdapter(mVideoAdapter);

        LinearLayoutManager reviewsManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mReviewsRecyclerView.setLayoutManager(reviewsManager);

        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        getSupportLoaderManager().initLoader(VIDEO_LOADER_ID, null, this);

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);

        checkFavoriteState();
        setFabIcon();

    }


    @OnClick(R.id.fab_fav)
    public void addToFavorite(View view) {
        ContentResolver contentResolver = getContentResolver();

        if (isFavorite == FavoriteEntry.MOVIE_NOT_FAVORITE) {
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE, FavoriteEntry.MOVIE_IS_FAVORITE);
            Uri movieItemUri = contentResolver.insert(FavoriteEntry.CONTENT_URI, contentValues);
            if (movieItemUri != null) {
                Toast.makeText(this, R.string.movie_added, Toast.LENGTH_LONG).show();
            }
            checkFavoriteState();
            setFabIcon();
        }else {
            Toast.makeText(this, R.string.movie_already_added, Toast.LENGTH_LONG).show();
        }
    }

    private void checkFavoriteState() {
        String[] projection = new String[]{FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE};

        Uri movieUri = FavoriteEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(mMovieId)).build();
        Cursor cursor = getContentResolver().query(
                movieUri,
                projection,
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToPosition(0)) {
                isFavorite = cursor.getInt(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_IS_FAVORITE));
            }
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void setFabIcon() {
        if (isFavorite == FavoriteEntry.MOVIE_IS_FAVORITE) {
            mActionButton.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mActionButton.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    private void getMovieData(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(JsonUtils.ID)) {
                mMovieId = intent.getIntExtra(JsonUtils.ID, 0);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
            }
            if (intent.hasExtra(JsonUtils.TITLE)) {
                String title = intent.getStringExtra(JsonUtils.TITLE);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_NAME, title);

                TextView titleTextView = findViewById(R.id.tv_title);
                titleTextView.setText(title);

            }
            if (intent.hasExtra(JsonUtils.POSTER)) {
                String poster = intent.getStringExtra(JsonUtils.POSTER);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_POSTER, poster);

                ImageView posterImageView = findViewById(R.id.iv_movie_poster);
                final String posterSize = "w500";
                Picasso.with(this)
                        .load(NetworkUtils.buildMoviePosterUrl(poster, posterSize))
                        .placeholder(R.drawable.pic_place_holder)
                        .error(R.drawable.pic_place_holder)
                        .into(posterImageView);

            }
            if (intent.hasExtra(JsonUtils.OVERVIEW)) {
                String overview = intent.getStringExtra(JsonUtils.OVERVIEW);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, overview);

                TextView overviewTextView = findViewById(R.id.tv_overview);
                overviewTextView.setText(overview);
            }
            if (intent.hasExtra(JsonUtils.VOTE_AVERAGE)) {
                String voteAverage = intent.getStringExtra(JsonUtils.VOTE_AVERAGE);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE, voteAverage);

                TextView voteAverageTextView = findViewById(R.id.tv_vote_average);
                voteAverageTextView.setText(voteAverage);
            }
            if (intent.hasExtra(JsonUtils.RELEASE_DATE)) {
                String releaseDate = intent.getStringExtra(JsonUtils.RELEASE_DATE);
                contentValues.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);

                TextView dateTextView = findViewById(R.id.tv_release_date);
                dateTextView.setText(releaseDate);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case VIDEO_LOADER_ID:
                return new VideoAsyncTaskLoader(this, mMovieId);
            case REVIEW_LOADER_ID:
                return new ReviewAsyncTaskLoader(this, mMovieId);
            default:
                throw new UnsupportedOperationException("Unknown loader id " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case VIDEO_LOADER_ID:
                mVideoAdapter.setVideoIds((List<String>) data);
                break;
            case REVIEW_LOADER_ID:
                mReviewsAdapter.setMovieReviews((List<MovieReview>) data);
                break;
            default:
                throw new UnsupportedOperationException("Unknown loader id " + loaderId);

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    private static class VideoAsyncTaskLoader extends AsyncTaskLoader<List<String>> {

        private List<String> mVideoList;
        private int mMovieId;

        public VideoAsyncTaskLoader(Context context, int movieId) {
            super(context);
            this.mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mVideoList != null) {
                deliverResult(mVideoList);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<String> loadInBackground() {
            URL requestUrl = NetworkUtils.buildVideoUrl(mMovieId);
            Log.i(LOG_TAG, "loadInBackground: " + requestUrl);

            if (requestUrl != null) {
                String videoJsonResponse = NetworkUtils.getJsonResponseFromHttpUrl(requestUrl);
                if (videoJsonResponse != null) {
                    return JsonUtils.extractVideosIds(videoJsonResponse);
                }
            }
            return null;
        }

        @Override
        public void deliverResult(List<String> data) {
            mVideoList = data;
            super.deliverResult(data);
        }
    }


    private static class ReviewAsyncTaskLoader extends AsyncTaskLoader<List<MovieReview>> {

        private List<MovieReview> movieReviews;
        private int mMovieId;

        public ReviewAsyncTaskLoader(Context context, int movieId) {
            super(context);
            this.mMovieId = movieId;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (movieReviews != null) {
                deliverResult(movieReviews);
            } else {
                forceLoad();
            }
        }

        @Override
        public List<MovieReview> loadInBackground() {
            URL requestUrl = NetworkUtils.buildReviewUrl(mMovieId);
            Log.i(LOG_TAG, "loadInBackground: " + requestUrl);

            if (requestUrl != null) {
                String reviewJsonResponse = NetworkUtils.getJsonResponseFromHttpUrl(requestUrl);
                if (reviewJsonResponse != null) {
                    return JsonUtils.extractMovieReviews(reviewJsonResponse);
                }
            }
            return null;

        }

        @Override
        public void deliverResult(List<MovieReview> data) {
            movieReviews = data;
            super.deliverResult(data);
        }


    }
}
