package com.company.connections;

import com.company.controllers.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class ConnectionHandler implements Runnable {

    Socket connectionSocket;

    BufferedReader reader;
    BufferedWriter writer;

    boolean closeSocket = true;

    public ConnectionHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;

        try {
            reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
        } catch (IOException e) {
            return;
        }
    }

    public void run() {
        String request = null;
        try {
            request = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(request);
        try {
            JSONObject req = new JSONObject(request);
            handleRequest(req);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (closeSocket) {
                reader.close();
                writer.close();
                connectionSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleRequest(JSONObject request) throws JSONException, SQLException {
        String function = request.getString("function");
        if(function.equals("signup")){
            signUp(request);
        }
        else if (function.equals("login")) {
            login(request);
        }
        else if(function.equals("showTickets")) {
            showTickets(request);
        }
        else if(function.equals("search")){
            search(request);
        }
        else if(function.equals("reserve")){
            reserve(request);
        }
        else {
            unhandledFunction();
        }
    }

    public void signUp(JSONObject request) throws SQLException {
        JSONObject response = new SignUpController(request).signup();
        sendReply(response);
    }

    public void login(JSONObject request) throws SQLException {
        JSONObject response = new LoginController(request).login();
        sendReply(response);
    }

    public void showTickets(JSONObject request) throws SQLException {
        JSONObject response = new MyTicketsController(request).showTickets();
        sendReply(response);
    }

    public void search(JSONObject request) throws SQLException {
        SearchController controller = new SearchController(request);
        JSONObject response;
        if(request.getString("detail").equals("saloon")){
            response = controller.searchSaloons();
            sendReply(response);
        }
        else if(request.getString("detail").equals("film")){
            response = controller.searchFilms();
            sendReply(response);
        }
        else if(request.getString("detail").equals("seat")){
            response = controller.searchSeats();
            sendReply(response);
        }
    }

    public void reserve(JSONObject request) throws SQLException {
        JSONObject response;
        if(request.getString("detail").equals("start")){
            response = new ReservationController(request).reserveStart();
            sendReply(response);
        }
        else if(request.getString("detail").equals("complete")){
            response = new ReservationController(request).reserveComplete();
            sendReply(response);
        }
    }

    public void unhandledFunction(){
        JSONObject response = new JSONObject();
        response.put("status", "404");
        response.put("error", "No such function!");
        sendReply(response);
    }

    public void sendReply(JSONObject response) {
        System.out.println(response);
        try {
            writer.write(response.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}