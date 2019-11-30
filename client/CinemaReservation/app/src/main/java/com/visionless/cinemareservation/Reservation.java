package com.visionless.cinemareservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.visionless.cinemareservation.model.Ticket;
import com.visionless.cinemareservation.ui.movieDetail.MovieDetail;
import com.visionless.cinemareservation.ui.movieList.MovieList;
import com.visionless.cinemareservation.ui.ticket.TicketFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Reservation extends AppCompatActivity {
    CountDownTimer countDownTimer;
    String movieName;
    String saloonName;
    String saloonId;
    String filmId;
    String username;
    String seatId;

    @Override
    public void onBackPressed() {
        this.countDownTimer.cancel();
        finish();
    }

    public String seatIdCalculator(String id){
        int seat = Integer.parseInt(id);
        seat = seat % 40 + 1;

        return ""+seat;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        this.getSupportActionBar().setTitle("Reservation");

        Intent intent = getIntent();
        movieName = intent.getStringExtra("filmName");
        saloonName = intent.getStringExtra("saloonName");
        saloonId = intent.getStringExtra("saloonId");
        filmId = intent.getStringExtra("filmId");
        seatId = intent.getStringExtra("seatId");
        username = intent.getStringExtra("username");

        TextView seat_label = (TextView)findViewById(R.id.seat);
        TextView film_label = (TextView)findViewById(R.id.film);
        TextView saloon_label = (TextView)findViewById(R.id.saloon);

        seat_label.setText(seatIdCalculator(seatId));
        film_label.setText(movieName);
        saloon_label.setText(saloonName);

        Button bttn = (Button) findViewById(R.id.button);
        bttn.setOnClickListener(new View.OnClickListener() {
            TextView email_label = (TextView)findViewById(R.id.email);

            @Override
            public void onClick(View v) {
                if(email_label.getText().toString() == null || email_label.getText().toString().equals("")) {
                    Toast.makeText(Reservation.this, "Email can not be null!", Toast.LENGTH_LONG).show();
                }
                else {
                    new ReservationCompleteSync(email_label.getText().toString()).execute();
                }
            }
        });

        countDownTimer=new CountDownTimer(60000,1000)
        {
            TextView timer_label = (TextView)findViewById(R.id.timer);
            @Override
            public void onTick(long millisUntilFinished) {
                if(millisUntilFinished/1000 < 10)
                    timer_label.setText("00:0"+String.valueOf(millisUntilFinished/1000));
                else
                    timer_label.setText("00:"+String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(Reservation.this, MovieDetail.class);
                intent.putExtra("filmName", movieName);
                intent.putExtra("filmId", filmId);
                intent.putExtra("saloonName", saloonName);
                intent.putExtra("saloonId", saloonId);
                intent.putExtra("username", username);
                Reservation.this.startActivity(intent);
            }
        }.start();
    }

    private class ReservationCompleteSync extends AsyncTask<Void, Void, JSONObject> {
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;
        private String email;

        public ReservationCompleteSync(String email){
            this.email = email;
            this.message = new JSONObject();
            try {
                message.put("function", "reserve");
                message.put("detail", "complete");
                message.put("username", username);
                message.put("seat", seatId);
                message.put("email", email);
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
                    Toast.makeText(Reservation.this, "Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Reservation.this, MainActivity.class);
                    intent.putExtra("username", username);
                    Reservation.this.startActivity(intent);
                } else {
                    Toast.makeText(Reservation.this, req.getString("error"), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Reservation.this, MovieDetail.class);
                    intent.putExtra("filmName", movieName);
                    intent.putExtra("filmId", filmId);
                    intent.putExtra("saloonName", saloonName);
                    intent.putExtra("saloonId", saloonId);
                    intent.putExtra("username", username);
                    Reservation.this.startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
