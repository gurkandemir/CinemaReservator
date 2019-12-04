package com.company.controllers;

import com.company.connections.ConnectionStarter;
import com.company.connections.DatabaseConnection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpController {
    private JSONObject request;

    public SignUpController(JSONObject request){
        this.request = request;
    }

    public JSONObject signup() throws SQLException {
        String username = request.getString("username");
        String password = request.getString("password");

        JSONObject response = new JSONObject();

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement preparedStatement = null;
        String SelectQuery = "select * from USER where username = '" + username +"'";
        String InsertQuery = "INSERT INTO USER (username, password) values" + "(?,?)";
        ConnectionStarter.dbWrite.P();
        try {
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(SelectQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                response.put("status", "400");
                response.put("error", "There exists a user with given username.");
            }
            else {

                preparedStatement = connection.prepareStatement(InsertQuery);

                preparedStatement.setString(  1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                preparedStatement.close();

                connection.commit();

                response.put("status", "200");
                response.put("username", username);
            }

        } catch (SQLException e) {
            ConnectionStarter.dbWrite.V();
            response.put("status", "400");
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            ConnectionStarter.dbWrite.V();
            response.put("status", "400");
            e.printStackTrace();
        } finally {
            connection.close();
        }
        ConnectionStarter.dbWrite.V();
        return response;
    }
}
