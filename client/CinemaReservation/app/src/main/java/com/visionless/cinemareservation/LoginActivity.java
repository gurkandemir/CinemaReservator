package com.visionless.cinemareservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.visionless.cinemareservation.ui.movieDetail.MovieDetail;
import com.visionless.cinemareservation.ui.movieList.MovieList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getSupportActionBar().setTitle("Login & Sign Up");

        Button login_bttn = (Button) findViewById(R.id.login_button);
        Button signup_bttn = (Button) findViewById(R.id.signup_button);
        login_bttn.setOnClickListener(new View.OnClickListener() {
            TextView username = (TextView)findViewById(R.id.username);
            TextView password = (TextView)findViewById(R.id.password);

            @Override
            public void onClick(View v) {
                if(username.getText().toString() == null || username.getText().toString().equals("") || password.getText().toString() == null || password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Username or password can not be null!", Toast.LENGTH_LONG).show();
                }
                else {
                    new LoginCompleteAsync(username.getText().toString(), password.getText().toString(), 0).execute();
                }
            }
        });

        signup_bttn.setOnClickListener(new View.OnClickListener() {
            TextView username = (TextView)findViewById(R.id.username);
            TextView password = (TextView)findViewById(R.id.password);

            @Override
            public void onClick(View v) {
                if(username.getText().toString() == null || username.getText().toString().equals("") || password.getText().toString() == null || password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Username or password can not be null!", Toast.LENGTH_LONG).show();
                }
                else {
                    new LoginCompleteAsync(username.getText().toString(), password.getText().toString(), 1).execute();
                }
            }
        });
    }

    private class LoginCompleteAsync extends AsyncTask<Void, Void, JSONObject>{
        String username;
        String password;
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;

        public LoginCompleteAsync(String username, String password, int type){
            this.username = username;
            this.password = password;
            message = new JSONObject();
            try {
                if(type == 0)
                    message.put("function", "login");
                else if(type == 1)
                    message.put("function", "signup");
                else
                    return;
                message.put("username", username);
                message.put("password", password);
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
                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    LoginActivity.this.startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, req.getString("error"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
