package com.company.connections;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:~/test";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";
    public static int numberOfFilms = 0;
    public DatabaseConnection(){}

    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
                    DB_PASSWORD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    public void initializeDatabase() throws SQLException{
        Connection connection = getDBConnection();

        String DropUserQuery = "DROP TABLE IF EXISTS USER";
        String DropSeatQuery = "DROP TABLE IF EXISTS SEAT";
        String DropFilmQuery = "DROP TABLE IF EXISTS FILM";
        String DropSaloonQuery = "DROP TABLE IF EXISTS SALOON";
        String DropReservationQuery = "DROP TABLE IF EXISTS RESERVATION";

        String CreateUserQuery = "CREATE TABLE USER(username varchar(255) PRIMARY KEY, password varchar(255))";
        String CreateSaloonQuery = "CREATE TABLE SALOON(id int(6), name varchar(255) PRIMARY KEY, location varchar(255))";
        String CreateFilmQuery = "CREATE TABLE FILM(id int(6), name varchar(255) PRIMARY KEY , date varchar (255), director varchar (255), saloon int (6), FOREIGN KEY (saloon) REFERENCES SALOON(id))";
        String CreateSeatQuery = "CREATE TABLE SEAT(id int(6) PRIMARY KEY, film int(6), available int(6) DEFAULT 0, username varchar (255),time varchar (255) DEFAULT  '00:00:00', FOREIGN KEY (film) REFERENCES FILM(id))";
        String CreateReservationQuery = "CREATE TABLE RESERVATION(id int(6) PRIMARY KEY AUTO_INCREMENT, username varchar(255), email varchar(255), seat int(6), date varchar (255), FOREIGN KEY (username) REFERENCES USER(username), FOREIGN KEY (seat) REFERENCES SEAT(id))";

        String InsertUserQuery = "INSERT INTO USER" + "(username, password) values" + "(?,?)";
        String InsertSaloonQuery = "INSERT INTO SALOON" + "(id, name, location) values" + "(?, ?,?)";
        String InsertFilmQuery = "INSERT INTO FILM" + "(id, name, date, director, saloon) values" + "(?,?,?,?,?)";
        String InsertSeatQuery = "INSERT INTO SEAT" + "(id, film) values" + "(?,?)";

        try{
            connection.setAutoCommit(false);

            PreparedStatement dropPreparedStatement = connection.prepareStatement(DropReservationQuery);
            dropPreparedStatement.executeUpdate();

            dropPreparedStatement = connection.prepareStatement(DropUserQuery);
            dropPreparedStatement.executeUpdate();

            dropPreparedStatement = connection.prepareStatement(DropSeatQuery);
            dropPreparedStatement.executeUpdate();

            dropPreparedStatement = connection.prepareStatement(DropFilmQuery);
            dropPreparedStatement.executeUpdate();

            dropPreparedStatement = connection.prepareStatement(DropSaloonQuery);
            dropPreparedStatement.executeUpdate();
            dropPreparedStatement.close();

            PreparedStatement createPreparedStatement = connection.prepareStatement(CreateUserQuery);
            createPreparedStatement.executeUpdate();

            createPreparedStatement = connection.prepareStatement(CreateSaloonQuery);
            createPreparedStatement.executeUpdate();

            createPreparedStatement = connection.prepareStatement(CreateFilmQuery);
            createPreparedStatement.executeUpdate();

            createPreparedStatement = connection.prepareStatement(CreateSeatQuery);
            createPreparedStatement.executeUpdate();

            createPreparedStatement = connection.prepareStatement(CreateReservationQuery);
            createPreparedStatement.executeUpdate();
            createPreparedStatement.close();

            PreparedStatement insertPreparedStatement = connection.prepareStatement(InsertUserQuery);
            insertPreparedStatement.setString(1, "gurkandemir");
            insertPreparedStatement.setString(2, "qwerty");
            insertPreparedStatement.executeUpdate();

            insertPreparedStatement = connection.prepareStatement(InsertSaloonQuery);
            insertPreparedStatement.setInt(1, 1);
            insertPreparedStatement.setString(2, "Cinemaximum");
            insertPreparedStatement.setString(3, "Kanyon AVM");
            insertPreparedStatement.executeUpdate();

            insertPreparedStatement = connection.prepareStatement(InsertFilmQuery);
            insertPreparedStatement.setInt(1, 1);
            insertPreparedStatement.setString(2, "Cep Herkulu: Naim Suleymanoglu");
            insertPreparedStatement.setString(3, "18.00");
            insertPreparedStatement.setString(4, "Ozer Feyzioglu");
            insertPreparedStatement.setInt(5, 1);
            insertPreparedStatement.executeUpdate();
            numberOfFilms++;
            insertPreparedStatement.setInt(1, 2);
            insertPreparedStatement.setString(2, "Midway");
            insertPreparedStatement.setString(3, "19.30");
            insertPreparedStatement.setString(4, "Roland Emmerich");
            insertPreparedStatement.setInt(5, 1);
            insertPreparedStatement.executeUpdate();
            numberOfFilms++;
            insertPreparedStatement.setInt(1, 3);
            insertPreparedStatement.setString(2, "Parasite");
            insertPreparedStatement.setString(3, "21.00");
            insertPreparedStatement.setString(4, "Bong Joon Ho");
            insertPreparedStatement.setInt(5, 1);
            insertPreparedStatement.executeUpdate();
            numberOfFilms++;
            insertPreparedStatement = connection.prepareStatement(InsertSeatQuery);
            for(int i = 0; i < 40; i++){
                insertPreparedStatement.setInt(1, i);
                insertPreparedStatement.setInt(2, 1);
                insertPreparedStatement.executeUpdate();

                insertPreparedStatement.setInt(1, i + 40);
                insertPreparedStatement.setInt(2, 2);
                insertPreparedStatement.executeUpdate();

                insertPreparedStatement.setInt(1, i + 80);
                insertPreparedStatement.setInt(2, 3);
                insertPreparedStatement.executeUpdate();
            }

            insertPreparedStatement.close();

            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}
