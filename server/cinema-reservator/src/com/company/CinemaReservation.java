package com.company;

import com.company.connections.*;
import java.sql.SQLException;

public class CinemaReservation {
    public static void main(String args[]) throws Exception {
        initializeDatabase();
        startConnection();
    }

    public static void initializeDatabase() throws SQLException {
        new DatabaseConnection().initializeDatabase();
    }

    public static void startConnection(){
        ConnectionStarter connectionStarter = new ConnectionStarter(8080);
        Thread thread = new Thread(connectionStarter);
        thread.start();
    }
}