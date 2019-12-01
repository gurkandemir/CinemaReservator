package com.visionless.cinemareservation.ui.ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.visionless.cinemareservation.CustomTicketListAdapter;
import com.visionless.cinemareservation.LoginActivity;
import com.visionless.cinemareservation.R;
import com.visionless.cinemareservation.model.Saloon;
import com.visionless.cinemareservation.model.Ticket;

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

public class TicketFragment extends Fragment {

    private TicketViewModel ticketViewModel;
    private ArrayList<Ticket> tickets;
    private ListView listView;
    private CustomTicketListAdapter listViewAdapter;
    String username;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ticketViewModel =
                ViewModelProviders.of(this).get(TicketViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ticket, container, false);

        initialize(root);
        username = getActivity().getIntent().getStringExtra("username");

        Thread thread = new Thread(new TicketThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return root;
    }


    private void initialize(View root) {
        tickets = new ArrayList<Ticket>();
        listView = (ListView) root.findViewById(R.id.ticket_list);
        listViewAdapter = new CustomTicketListAdapter(getActivity(),tickets);
        listView.setAdapter(listViewAdapter);
    }

    private class TicketThread implements Runnable{
        private Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        private JSONObject message;

        public void run(){
            message = new JSONObject();
            try {
                message.put("function", "showTickets");
                message.put("username", username);
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
                    JSONArray tickets1 = req.getJSONArray("tickets");
                    for (int index = 0; index < tickets1.length(); index++) {
                        JSONObject object = tickets1.getJSONObject(index);
                        Ticket ticket = new Ticket((object.getInt("seat") % 40) + 1, object.getString("film"), object.getString("saloon"), object.getString("date") ,object.getString("time"));
                        tickets.add(ticket);
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