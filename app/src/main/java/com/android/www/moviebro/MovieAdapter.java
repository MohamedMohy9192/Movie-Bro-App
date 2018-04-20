package com.android.www.moviebro;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.www.moviebro.model.Movie;
import com.android.www.moviebro.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovieData;
    private Context mContext;

    private MovieAdapterOnClickHandler mMovieAdapterOnClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onListItemClick(int movieId,
                             String title,
                             String poster,
                             String overview,
                             String voteAverage,
                             String releaseDate);
    }

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mMovieAdapterOnClickHandler = clickHandler;

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(mContext)
                .inflate(R.layout.movie_list_item, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);

        String moviePosterPath = movie.getPosterImage();
        String posterSize = "w342";
        String moviePosterUrl = NetworkUtils.buildMoviePosterUrl(moviePosterPath, posterSize);

        Picasso.with(mContext)
                .setIndicatorsEnabled(true);

        Picasso.with(mContext)
                .load(moviePosterUrl)
                .placeholder(R.drawable.pic_place_holder)
                .error(R.drawable.pic_place_holder)
                .into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;

        return mMovieData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        this.mMovieData = movieData;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_movie_poster)
        public ImageView mMoviePosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie movie = mMovieData.get(position);

            mMovieAdapterOnClickHandler.onListItemClick(
                    movie.getId(),
                    movie.getOriginalTitle(),
                    movie.getPosterImage(),
                    movie.getOverview(),
                    movie.getVoteAverage(),
                    movie.getReleaseDate());
        }
    }
}
