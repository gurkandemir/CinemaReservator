package com.company.controllers;

import com.company.connections.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchController {
    private JSONObject request;

    public SearchController(JSONObject request){
        this.request = request;
    }

    public JSONObject searchSaloons() throws SQLException {
        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        ResultSet rs = null;
        String SelectQuery = "select * from SALOON";

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            connection.setAutoCommit(false);
            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            rs = selectPreparedStatement.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("name", rs.getString("name"));
                obj.put("location", rs.getString("location"));

                array.put(obj);
            }

            selectPreparedStatement.close();
            connection.commit();
            response.put("status", "200");
        } catch (SQLException e) {
            response.put("status", "400");
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            response.put("status", "400");
            e.printStackTrace();
        } finally {
            connection.close();
        }

        response.put("saloons", array);
        return response;
    }

    public JSONObject searchFilms() throws SQLException {
        int saloon = request.getInt("saloon");

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        ResultSet rs = null;
        String SelectQuery = "select * from FILM where saloon = "+saloon;

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            connection.setAutoCommit(false);
            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            rs = selectPreparedStatement.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("name", rs.getString("name"));
                obj.put("date", rs.getString("date"));
                obj.put("director", rs.getString("director"));

                array.put(obj);
            }
            selectPreparedStatement.close();

            connection.commit();
            response.put("status", "200");
        } catch (SQLException e) {
            response.put("status", "400");
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            response.put("status", "400");
            e.printStackTrace();
        } finally {
            connection.close();
        }

        response.put("films", array);
        return response;
    }

    public JSONObject searchSeats() throws SQLException {
        String film = request.getString("film");

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        ResultSet rs = null;
        String SelectQuery = "select * from SEAT where film = '"+ film+"'";

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            connection.setAutoCommit(false);
            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            rs = selectPreparedStatement.executeQuery();

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("available", rs.getInt("available"));

                array.put(obj);
            }

            selectPreparedStatement.close();

            connection.commit();
            response.put("status", "200");
        } catch (SQLException e) {
            response.put("status", "400");
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            response.put("status", "400");
            e.printStackTrace();
        } finally {
            connection.close();
        }

        response.put("seats", array);
        return response;
    }
}
