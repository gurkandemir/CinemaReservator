package com.visionless.cinemareservation.ui.movie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.visionless.cinemareservation.CustomHouseListAdapter;
import com.visionless.cinemareservation.LoginActivity;
import com.visionless.cinemareservation.R;
import com.visionless.cinemareservation.model.Saloon;
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
import java.util.ArrayList;

public class MovieFragment extends Fragment {

    private MovieViewModel movieViewModel;
    private ArrayList<Saloon> saloons;
    private ListView listView;
    private CustomHouseListAdapter listViewAdapter;
    String username;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        movieViewModel =
                ViewModelProviders.of(this).get(MovieViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saloon, container, false);

        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        initialize(root);

        Thread thread = new Thread(new SaloonThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return root;
    }


    private void initialize(View root) {
        saloons = new ArrayList<Saloon>();
        listView = (ListView) root.findViewById(R.id.house_list);
        listViewAdapter = new CustomHouseListAdapter(getActivity(), saloons);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Saloon item = (Saloon) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), ""+item.getName(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent (getActivity(), MovieList.class);
                i.putExtra("saloonName", item.getName());
                i.putExtra("saloonId", ""+item.getId());
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

    private class SaloonThread implements Runnable{
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;

        public void run(){
            message = new JSONObject();
            try {
                message.put("function", "search");
                message.put("detail", "saloon");
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
                    JSONArray saloons1 = req.getJSONArray("saloons");
                    for (int index = 0; index < saloons1.length(); index++) {
                        JSONObject object = saloons1.getJSONObject(index);
                        Saloon saloon = new Saloon(object.getInt("id"), object.getString("name"), object.getString("location"));
                        saloons.add(saloon);
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
