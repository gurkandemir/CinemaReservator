package com.company.controllers;

import com.company.connections.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyTicketsController {
    private JSONObject request;

    public MyTicketsController(JSONObject request){
        this.request = request;
    }

    public JSONObject showTickets() throws SQLException {
        String username = request.getString("username");

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        String SelectReservationQuery = "select * from RESERVATION, SEAT, FILM, SALOON where RESERVATION.seat = SEAT.id and SEAT.film = FILM.id and FILM.saloon = SALOON.id and RESERVATION.username = '"+username+"' order by id desc";

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            connection.setAutoCommit(false);
            selectPreparedStatement = connection.prepareStatement(SelectReservationQuery);
            ResultSet rs = selectPreparedStatement.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("seat", rs.getInt("RESERVATION.seat"));
                obj.put("date", rs.getString("RESERVATION.date"));
                obj.put("film", rs.getString("FILM.name"));
                obj.put("saloon", rs.getString("SALOON.name"));
                obj.put("time", rs.getString("FILM.date"));

                array.put(obj);
            }

            selectPreparedStatement.close();
            response.put("status", "200");
            connection.commit();
        } catch (SQLException e) {
            response.put("status", "400");
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            response.put("status", "400");
            e.printStackTrace();
        } finally {
            connection.close();
        }

        response.put("tickets", array);
        return response;
    }
}
