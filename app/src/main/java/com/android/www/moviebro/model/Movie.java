package com.android.www.moviebro.model;

/**
 * Created by OWNER on 2/17/2018.
 */

public class Movie {

    private int id;
    private String originalTitle;
    private String posterImage;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public Movie(int id, String originalTitle, String posterImage, String overview, String voteAverage, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterImage = posterImage;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public void setPosterImage(String posterImage) {
        this.posterImage = posterImage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
