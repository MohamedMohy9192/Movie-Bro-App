package com.android.www.moviebro;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.www.moviebro.model.MovieReview;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<MovieReview> movieReviews;

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);

        return new ReviewsViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        MovieReview movieReview = movieReviews.get(position);

        String reviewAuthor = movieReview.getAuthor();
        holder.reviewAuthorTextView.setText(reviewAuthor);

        String reviewContent = movieReview.getContent();
        holder.reviewContentTextView.setText(reviewContent);
    }

    @Override
    public int getItemCount() {
        if (movieReviews == null) return 0;

        return movieReviews.size();
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_review_author)
        TextView reviewAuthorTextView;

        @BindView(R.id.tv_review_content)
        TextView reviewContentTextView;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }
    }
}
