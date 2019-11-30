package com.company.controllers;

import com.company.connections.DatabaseConnection;
import javafx.scene.chart.PieChart;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private JSONObject request;

    public LoginController(JSONObject request){
        this.request = request;
    }

    public JSONObject login() throws SQLException {
        String username = request.getString("username");
        String password = request.getString("password");

        JSONObject response = new JSONObject();

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement selectPreparedStatement = null;
        ResultSet rs = null;
        String SelectQuery = "select * from USER where username = '" + username +"' and password = '"+ password + "'";
        try {
            connection.setAutoCommit(false);
            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            rs = selectPreparedStatement.executeQuery();

            if (rs.next()) {
                response.put("status", "200");
                response.put("username", username);
            } else {
                response.put("status", "400");
                response.put("error", "No such user!");
            }

            selectPreparedStatement.close();

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

        return response;
    }
}
