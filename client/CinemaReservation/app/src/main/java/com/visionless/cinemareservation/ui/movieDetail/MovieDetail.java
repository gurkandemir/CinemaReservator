package com.visionless.cinemareservation.ui.movieDetail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.visionless.cinemareservation.R;
import com.visionless.cinemareservation.Reservation;
import com.visionless.cinemareservation.ui.movieList.MovieList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MovieDetail extends AppCompatActivity {
    GridLayout gridLayout;
    Button bttn;
    String movieName;
    String saloonName;
    String saloonId;
    String filmId;
    String username;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MovieDetail.this, MovieList.class);
        intent.putExtra("filmName", movieName);
        intent.putExtra("filmId", filmId);
        intent.putExtra("saloonName", saloonName);
        intent.putExtra("saloonId", saloonId);
        intent.putExtra("username", username);
        MovieDetail.this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        movieName = intent.getStringExtra("filmName");
        saloonName = intent.getStringExtra("saloonName");
        saloonId = intent.getStringExtra("saloonId");
        filmId = intent.getStringExtra("filmId");
        username = intent.getStringExtra("username");
        this.getSupportActionBar().setTitle(saloonName+" -> "+movieName);

        gridLayout = findViewById(R.id.grid);
        gridLayout.setColumnCount(4);
        gridLayout.setRowCount(10);

        Thread thread = new Thread(new SeatThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class SeatThread implements Runnable{
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;

        public void run(){
            message = new JSONObject();
            try {
                message.put("function", "search");
                message.put("detail", "seat");
                message.put("film", filmId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                socket = new Socket("172.20.10.3", 8080);
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
                    JSONArray seats1 = req.getJSONArray("seats");
                    for (int i = 0; i< seats1.length(); i++) {
                        final JSONObject object = seats1.getJSONObject(i);
                        final int id = object.getInt("id");
                        bttn = new Button(MovieDetail.this);
                        bttn.setText("" + ((object.getInt("id") % 40) + 1));
                        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                        param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                        param.setMargins(11, 5, 11, 5);
                        bttn.setLayoutParams(param);
                        bttn.setTextColor(Color.BLACK);
                        if(object.getInt("available") == 0) {
                            bttn.setBackgroundColor(Color.GREEN);
                            bttn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ReservationStartSync(id+"").execute();
                                }
                            });
                        }
                        else
                            bttn.setBackgroundColor(Color.RED);

                        gridLayout.addView(bttn, i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReservationStartSync extends AsyncTask<Void, Void, JSONObject>{
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;
        private String seatId;

        public ReservationStartSync(String seatId){
            this.seatId = seatId;
            this.message = new JSONObject();
            try {
                message.put("function", "reserve");
                message.put("detail", "start");
                message.put("username", username);
                message.put("seat", seatId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                socket = new Socket("172.20.10.3", 8080);
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

            return req;
        }

        protected void onPostExecute(JSONObject req) {
            try {
                if (req.getString("status").equals("200")) {
                    Toast.makeText(MovieDetail.this, "Reservation starts", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MovieDetail.this, Reservation.class);
                    intent.putExtra("filmName", movieName);
                    intent.putExtra("filmId", filmId);
                    intent.putExtra("saloonName", saloonName);
                    intent.putExtra("saloonId", saloonId);
                    intent.putExtra("seatId", seatId);
                    intent.putExtra("username", username);
                    MovieDetail.this.startActivity(intent);
                } else {
                    Toast.makeText(MovieDetail.this, req.getString("error"), Toast.LENGTH_LONG).show();
                    //finish();
                    //startActivity(getIntent());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
