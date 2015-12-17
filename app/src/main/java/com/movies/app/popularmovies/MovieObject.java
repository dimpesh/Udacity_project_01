package com.movies.app.popularmovies;

import java.io.Serializable;

/**
 * Created by DIMPESH : ${month}
 */
public class MovieObject implements Serializable{
    String title;
    String overview;
    String release_date;
    String vote_average;
    String poster_path;

    public MovieObject(){}
    public MovieObject(String poster_path)
    {
        this.poster_path=poster_path;
    }
}
