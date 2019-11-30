package com.company.controllers;

import com.company.connections.ConnectionStarter;
import com.company.connections.DatabaseConnection;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ReservationController {
    private JSONObject request;

    public ReservationController(JSONObject request){
        this.request = request;
    }

    public JSONObject reserveStart() throws SQLException {
        String username = request.getString("username");
        String seat = request.getString("seat");

        ConnectionStarter.seats[Integer.parseInt(seat)].P();

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement preparedStatement = null;
        String SelectQuery = "select * from SEAT where available = 0 and id = " + Integer.parseInt(seat);
        JSONObject response = new JSONObject();
        try {
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(SelectQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                String lastModified = rs.getString("time");
                String now = new SimpleDateFormat("HH:mm:ss").format(new Date());
                if(lastModified.equals("00:00:00")){
                    ConnectionStarter.dbWrite.P();

                    String UpdateQuery = "UPDATE SEAT SET time = '"+ now +"', username = '" + username+"' where id = " + Integer.parseInt(seat);

                    preparedStatement = connection.prepareStatement(UpdateQuery);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.commit();
                    ConnectionStarter.dbWrite.V();

                    response.put("status", "200");
                }
                else {
                    String[] splittedNow = now.split(":", 3);
                    String[] splittedLastModified = lastModified.split(":", 3);

                    int duration = (3600 * Integer.parseInt(splittedNow[0]) + 60 * Integer.parseInt(splittedNow[1]) + Integer.parseInt(splittedNow[2])) - (3600 * Integer.parseInt(splittedLastModified[0]) + 60 * Integer.parseInt(splittedLastModified[1]) + Integer.parseInt(splittedLastModified[2]));
                    if(duration < 60){
                        response.put("status", "400");
                        response.put("error", "Seat is in progress.");
                    }
                    else{
                        ConnectionStarter.dbWrite.P();
                        String UpdateQuery = "UPDATE SEAT SET time = '"+ now +"', username = '" + username+"' where id = " + Integer.parseInt(seat);

                        preparedStatement = connection.prepareStatement(UpdateQuery);
                        preparedStatement.executeUpdate();
                        preparedStatement.close();
                        connection.commit();
                        ConnectionStarter.dbWrite.V();

                        response.put("status", "200");
                    }
                }
            } else {
                response.put("status", "400");
                response.put("error", "Already reserved or no such seat.");
            }
        } catch (SQLException e) {
            ConnectionStarter.dbWrite.V();
            System.out.println("Exception Message " + e.getLocalizedMessage());
            response.put("status", "400");
            response.put("error", "Internal error.");
        } catch (Exception e) {
            ConnectionStarter.dbWrite.V();
            e.printStackTrace();
            response.put("status", "400");
            response.put("error", "Internal error.");
        } finally {
            connection.close();
        }

        ConnectionStarter.seats[Integer.parseInt(seat)].V();
        return response;
    }

    public JSONObject reserveComplete() throws SQLException {
        String username = request.getString("username");
        String email = request.getString("email");
        String seat = request.getString("seat");

        ConnectionStarter.seats[Integer.parseInt(seat)].P();

        Connection connection = new DatabaseConnection().getDBConnection();
        PreparedStatement preparedStatement = null;
        String SelectQuery = "select * from SEAT where available = 0 and username = '" + username+ "' and id = " + Integer.parseInt(seat);
        String InsertQuery = "INSERT INTO RESERVATION (username, email, seat, date) values" + "(?,?,?,?)";
        String UpdateQuery = "UPDATE SEAT SET available = 1 where username = '"+ username +"' and id = " + Integer.parseInt(seat);
        JSONObject response = new JSONObject();
        try {
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(SelectQuery);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                ConnectionStarter.dbWrite.P();
                preparedStatement = connection.prepareStatement(InsertQuery);

                preparedStatement.setString(  1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setInt(3, Integer.parseInt(seat));
                preparedStatement.setString(4, LocalDate.now().toString());
                preparedStatement.executeUpdate();
                preparedStatement.close();

                preparedStatement = connection.prepareStatement(UpdateQuery);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.commit();

                ConnectionStarter.dbWrite.V();
                response.put("status", "200");
            } else {
                response.put("status", "400");
                response.put("error", "Not allowed.");
            }

        } catch (SQLException e) {
            ConnectionStarter.dbWrite.V();
            response.put("status", "400");
            response.put("error", "Internal error.");
        } catch (Exception e) {
            ConnectionStarter.dbWrite.V();
            e.printStackTrace();
            response.put("status", "400");
            response.put("error", "Internal error.");
        } finally {
            connection.close();
        }

        ConnectionStarter.seats[Integer.parseInt(seat)].V();

        return response;
    }
}
