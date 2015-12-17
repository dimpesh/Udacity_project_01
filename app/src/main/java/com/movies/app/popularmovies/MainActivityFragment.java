package com.movies.app.popularmovies;

import android.content.Intent;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String popular="popularity.desc";
    String top="vote_average.desc";
    private MovieAdapter movieAdapter;
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
     //   super.onCreateOptionsMenu(menu, inflater);
       inflater.inflate(R.menu.fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_popular)
        {
            new FetchMovieTask().execute(popular);
            return true;

        }
        if(id==R.id.action_top_rated)
        {
            new FetchMovieTask().execute(top);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //String []moviesArray= {"MONDAY","TUESDAY","WENESDAY","THURSDAY"};
        final MovieObject []movieObjects={new MovieObject("a"),new MovieObject("b")};
        //List<String>listmovie=new ArrayList<String>(Arrays.asList(moviesArray));
        List<MovieObject>listmovie=new ArrayList<MovieObject>(Arrays.asList(movieObjects));
        //movieAdapter=new MovieAdapter(getActivity(),R.layout.grid_item_movies,R.id.grid_item_movies_imageview,listmovie);
        movieAdapter=new MovieAdapter(getActivity(),R.layout.grid_item_movies,R.id.grid_item_movies_imageview,listmovie);
        View rootview=inflater.inflate(R.layout.fragment_main,container,false);
        new FetchMovieTask().execute("popularity.desc");
        GridView gridView= (GridView) rootview.findViewById(R.id.gridview_movies);

        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            MovieObject movieClicked= (MovieObject) movieAdapter.getItem(position);
                //String overview=movieClicked.overview;
                //Toast.makeText(getActivity(),overview,Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(),DetailActivity.class);
                Bundle mBundle=new Bundle();

                mBundle.putSerializable("MovieObjectSent",movieClicked);
                intent.putExtras(mBundle);
                startActivity(intent);


            }
        });
/*
        int width=gridView.getWidth()/2;
        gridView.setColumnWidth(width);
        gridView.setVerticalSpacing(0);
        gridView.setHorizontalSpacing(0);
 */
        return rootview;
        //return inflater.inflate(R.layout.fragment_main, container, false);
    }

    class FetchMovieTask extends AsyncTask<String,Void,MovieObject[]>
    {
        MovieObject [] movieObjects=null;
   //     String []str=null;

        @Override
        protected void onPostExecute(MovieObject[] str) {
        if(str!=null)
                movieAdapter.clear();

            for(MovieObject m : str)
            {
                movieAdapter.add(m);
            }
        }

        @Override
        protected MovieObject[] doInBackground(String... strings) {



            final String movieBaseUrl="http://api.themoviedb.org/3/discover/movie?";
            String api_key="null";
            String API_PARAM="api_key";
            String TYPE_PARAM="sort_by";
            String type=strings[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJSONStr = null;
            try {

                Uri buildUri=Uri.parse(movieBaseUrl).buildUpon().appendQueryParameter(TYPE_PARAM,type).appendQueryParameter(API_PARAM,api_key).build();
                URL url=new URL(buildUri.toString());
                Log.v("MY URL",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    movieJSONStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    movieJSONStr = null;
                }
                movieJSONStr = buffer.toString();
                // Log.v("MY JSON OUTPUT", forecastJSONStr);
                String title="title";
                String vote_average="vote_avergae";
                String overview="overview";
                JSONObject movieJSONObject=new JSONObject(movieJSONStr);
                JSONArray movieJSONArray=movieJSONObject.optJSONArray("results");
//                movieObjects=new MovieObject[movieJSONArray.length()];
                //str=new String[movieJSONArray.length()];
                movieObjects=new MovieObject[movieJSONArray.length()];
                for(int i=0;i<movieJSONArray.length();i++)
                {
                    movieObjects[i]=new MovieObject();
                    JSONObject jsonObject=movieJSONArray.getJSONObject(i);
                    movieObjects[i].title=jsonObject.optString("title").toString();
                    movieObjects[i].overview=jsonObject.optString("overview").toString();
                    movieObjects[i].poster_path=jsonObject.optString("poster_path").toString();
                    movieObjects[i].release_date = jsonObject.getString("release_date").toString();
                    movieObjects[i].vote_average=jsonObject.getString("vote_average").toString();
                   // str[i] = jsonObject.getString("poster_path");
                }
                /*
                for(String s : str)
                {
                    Log.v("POSTER PATH",s);
                }
                */

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movieObjects;


          //  return null;
        }
    }
}
