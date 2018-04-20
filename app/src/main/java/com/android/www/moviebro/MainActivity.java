package com.android.www.moviebro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.www.moviebro.data.MovieContract.FavoriteEntry;
import com.android.www.moviebro.model.Movie;
import com.android.www.moviebro.settings.SettingsActivity;
import com.android.www.moviebro.utilities.JsonUtils;
import com.android.www.moviebro.utilities.NetworkUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int MOVIE_LOADER_ID = 1;
    private static final int MOVIE_CURSOR_ID = 2;

    private static final String SORT_ORDER_BUNDLE_KEY = "sort_order";

    private MovieAdapter mMovieAdapter;

    @BindView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_indicator)
    ProgressBar mIndicatorProgressBar;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);

        mRecyclerView.setAdapter(mMovieAdapter);


        setupSharedPreferences();

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        Log.i(LOG_TAG, "with: " + width);
        int nColumns = width / widthDivider;
        Log.i(LOG_TAG, "numberOfColumns: " + nColumns);
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void resetMoveData(String sortOrderPreference) {
        showMovieDataView();

        if (sortOrderPreference.equals(getString(R.string.pref_sort_order_favorite_value))) {
            getSupportLoaderManager().restartLoader(MOVIE_CURSOR_ID, null, this);
        } else {

            Bundle sortOrderBundle = new Bundle();
            sortOrderBundle.putString(SORT_ORDER_BUNDLE_KEY, sortOrderPreference);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, sortOrderBundle, this);
        }
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void invalidateData() {
        mMovieAdapter.setMovieData(null);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {
            case MOVIE_LOADER_ID:
                String sortOrderPreference = null;
                if (args != null) {
                    if (args.containsKey(SORT_ORDER_BUNDLE_KEY)) {
                        sortOrderPreference = args.getString(SORT_ORDER_BUNDLE_KEY);
                    }
                }

                return new MovieAsyncTaskLoader(this, sortOrderPreference);
            case MOVIE_CURSOR_ID:
                Log.i(LOG_TAG, "onCreateLoader: MOVIE_CURSOR_ID");
                return new CursorLoader(this,
                        FavoriteEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);


            default:
                throw new UnsupportedOperationException("Unknown loader id: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object movies) {
        mIndicatorProgressBar.setVisibility(View.INVISIBLE);

        int loaderId = loader.getId();

        if (movies != null) {
            showMovieDataView();
            switch (loaderId) {
                case MOVIE_LOADER_ID:
                    mMovieAdapter.setMovieData((List<Movie>) movies);
                    break;
                case MOVIE_CURSOR_ID:
                    Log.i(LOG_TAG, "onLoadFinished: MOVIE_CURSOR_ID");
                    Cursor cursor = (Cursor) movies;

                    List<Movie> listMovies = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        int movieServerId = cursor.getInt(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_ID));
                        String movieName = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_NAME));
                        String moviePoster = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_POSTER));
                        String overview = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_OVERVIEW));
                        String voteAverage = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE));
                        String releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE));

                        Movie movie = new Movie(movieServerId, movieName, moviePoster, overview, voteAverage, releaseDate);
                        listMovies.add(movie);
                    }
                    mMovieAdapter.setMovieData(listMovies);
                    cursor.moveToPosition(-1);
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown loader id: " + loaderId);
            }
        } else {
            showErrorMessage();
        }


    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i(LOG_TAG, "onLoaderReset: ");
        mMovieAdapter.setMovieData(null);
    }

    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);


        String sortOrderPreference = sharedPreferences.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_popular_value));

        loadMovieData(sortOrderPreference);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadMovieData(String sortOrderPreference) {
        showMovieDataView();


        if (sortOrderPreference.equals(getString(R.string.pref_sort_order_favorite_value))) {
            Log.i(LOG_TAG, "loadMovieData: pref_sort_order_favorite_value");
            getSupportLoaderManager().initLoader(MOVIE_CURSOR_ID, null, this);
        } else {
            Bundle sortOrderBundle = new Bundle();
            sortOrderBundle.putString(SORT_ORDER_BUNDLE_KEY, sortOrderPreference);

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, sortOrderBundle, this);

        }

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_order_key))) {
            String sortOrderPreference = sharedPreferences.getString(
                    key, getString(R.string.pref_sort_order_popular_value));
            invalidateData();
            resetMoveData(sortOrderPreference);
        }
    }

    @Override
    public void onListItemClick(int movieId, String title, String poster, String overview, String voteAverage, String releaseDate) {
        Intent openDetailActivity = new Intent(this, DetailActivity.class);
        openDetailActivity.putExtra(JsonUtils.ID, movieId)
                .putExtra(JsonUtils.TITLE, title)
                .putExtra(JsonUtils.POSTER, poster)
                .putExtra(JsonUtils.OVERVIEW, overview)
                .putExtra(JsonUtils.VOTE_AVERAGE, voteAverage)
                .putExtra(JsonUtils.RELEASE_DATE, releaseDate);
        startActivity(openDetailActivity);
    }

    private static class MovieAsyncTaskLoader extends AsyncTaskLoader<List<Movie>> {

        private List<Movie> mMovies;

        private String mSortOrderPreference;

        private WeakReference<MainActivity> mMainActivityWeakReference;

        public MovieAsyncTaskLoader(MainActivity mainActivity, String sortOrderPreference) {
            super(mainActivity);
            this.mMainActivityWeakReference = new WeakReference<>(mainActivity);
            this.mSortOrderPreference = sortOrderPreference;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (mMovies != null) {
                deliverResult(mMovies);
            } else {
                if (mMainActivityWeakReference.get() != null) {
                    mMainActivityWeakReference.get().mIndicatorProgressBar.setVisibility(View.VISIBLE);
                }
                forceLoad();
            }
        }

        @Override
        public List<Movie> loadInBackground() {

            URL requestUrl = NetworkUtils.buildUrl(mSortOrderPreference);
            Log.i(LOG_TAG, "loadInBackground: " + requestUrl);

            if (requestUrl != null) {
                String movieJsonResponse = NetworkUtils.getJsonResponseFromHttpUrl(requestUrl);
                if (movieJsonResponse != null) {
                    return JsonUtils.extractMovieFromJson(movieJsonResponse);
                }
            }
            return null;
        }

        @Override
        public void deliverResult(List<Movie> data) {
            this.mMovies = data;
            super.deliverResult(data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent openDetailsActivity = new Intent(this, SettingsActivity.class);
            startActivity(openDetailsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
