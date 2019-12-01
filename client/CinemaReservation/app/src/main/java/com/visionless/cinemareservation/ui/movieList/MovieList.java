package com.visionless.cinemareservation.ui.movieList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.visionless.cinemareservation.CustomMovieListAdapter;
import com.visionless.cinemareservation.LoginActivity;
import com.visionless.cinemareservation.MainActivity;
import com.visionless.cinemareservation.R;
import com.visionless.cinemareservation.model.Film;
import com.visionless.cinemareservation.ui.movie.MovieFragment;
import com.visionless.cinemareservation.ui.movieDetail.MovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MovieList extends AppCompatActivity {

    private ArrayList<Film> films;
    private ListView listView;
    private CustomMovieListAdapter listViewAdapter;
    String saloonName;
    String saloonId;
    String username;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MovieList.this, MainActivity.class);
        intent.putExtra("saloonName", saloonName);
        intent.putExtra("saloonId", saloonId);
        intent.putExtra("username", username);
        MovieList.this.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Intent i = getIntent();
        saloonName = i.getStringExtra("saloonName");
        saloonId = i.getStringExtra("saloonId");
        username = i.getStringExtra("username");
        this.getSupportActionBar().setTitle(saloonName);

        initialize();
        Thread thread = new Thread(new FilmThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        films = new ArrayList<Film>();
        listView = (ListView) findViewById(R.id.house_list);
        listViewAdapter = new CustomMovieListAdapter(this, films);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Film item = (Film) parent.getItemAtPosition(position);
                Toast.makeText(MovieList.this, ""+item.getName(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), MovieDetail.class);

                i.putExtra("filmName", item.getName());
                i.putExtra("filmId", ""+item.getId());
                i.putExtra("saloonName", saloonName);
                i.putExtra("saloonId", saloonId);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

    private class FilmThread implements Runnable{
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;

        @Override
        public void run() {
            message = new JSONObject();
            try {
                message.put("function", "search");
                message.put("detail", "film");
                message.put("saloon", Integer.parseInt(saloonId));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                socket = new Socket(LoginActivity.connection.getHost(), LoginActivity.connection.getPort());
                writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject req = null;

            try {
                writer.write(message.toString());
                writer.newLine();
                writer.flush();
                req = new JSONObject(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if(req.getString("status").equals("200")) {
                    JSONArray films1 = req.getJSONArray("films");
                    for (int index = 0; index < films1.length(); index++) {
                        JSONObject object = films1.getJSONObject(index);
                        Film film = new Film(object.getInt("id"),object.getString("name"), object.getString("date"), object.getString("director"));
                        films.add(film);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
