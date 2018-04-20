package com.android.www.moviebro;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohamed on 3/15/2018.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<String> mVideoIdList;
    private Context mContext;
    private String VIDEO_API_KEY = BuildConfig.VIDEO_API_KEY;

    public VideoAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        final String videoId = mVideoIdList.get(position);

        holder.youTubeThumbnailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (YouTubeIntents.canResolvePlayVideoIntentWithOptions(mContext)) {
                    mContext.startActivity(YouTubeIntents.createPlayVideoIntentWithOptions(
                            mContext, videoId, true, true));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mVideoIdList == null) return 0;
        return mVideoIdList.size();
    }

    public void setVideoIds(List<String> videoIds) {
        this.mVideoIdList = videoIds;
        notifyDataSetChanged();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements YouTubeThumbnailView.OnInitializedListener {

        @BindView(R.id.you_tube_thumbnail_view)
        YouTubeThumbnailView youTubeThumbnailView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            youTubeThumbnailView.initialize(VIDEO_API_KEY, this);

        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
            int position = getAdapterPosition();
            String movieId = mVideoIdList.get(position);

            youTubeThumbnailLoader.setVideo(movieId);
            youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailLoader.release();
                }

                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }
            });

        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

        }
    }

}
