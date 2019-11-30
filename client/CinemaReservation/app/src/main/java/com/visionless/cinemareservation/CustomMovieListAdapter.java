package com.visionless.cinemareservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.visionless.cinemareservation.model.Film;

import java.util.ArrayList;

public class CustomMovieListAdapter extends ArrayAdapter<Film> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final ArrayList<Film> movies;

    public CustomMovieListAdapter(Context context, ArrayList<Film> movies) {
        super(context,0, movies);
        this.context = context;
        this.movies = movies;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Film getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.movie_list_item, null);

            holder = new ViewHolder();
            holder.movieNameLabel = (TextView) convertView.findViewById(R.id.movie_name_label);
            holder.dateNameLabel = (TextView) convertView.findViewById(R.id.date_name_label);
            holder.directorNameLabel = (TextView) convertView.findViewById(R.id.director_name_label);
            convertView.setTag(holder);

        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        Film movie = movies.get(position);
        if(movie != null){
            holder.movieNameLabel.setText(movie.getName());
            holder.dateNameLabel.setText(movie.getDate());
            holder.directorNameLabel.setText(movie.getDirector());
        }
        return convertView;
    }

    //View Holder Pattern for better performance
    private static class ViewHolder {
        TextView movieNameLabel;
        TextView dateNameLabel;
        TextView directorNameLabel;
    }
}