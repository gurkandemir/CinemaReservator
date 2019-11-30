package com.company.connections;

import com.company.utils.Mutex;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionStarter implements Runnable{
    private ServerSocket socket;
    public static Mutex dbWrite;
    public static Mutex[] seats;

    public ConnectionStarter(int port){
        seats = new Mutex[40 * DatabaseConnection.numberOfFilms];
        for(int i = 0; i < seats.length; i++){
            seats[i] = new Mutex(true);
        }

        dbWrite = new Mutex(true);

        try {
            socket = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket connectionSocket = socket.accept();
                new Thread(new ConnectionHandler(connectionSocket)).start();
            } catch (IOException ex) {
                System.err.println("Server aborted:" + ex);
            }
        }
    }
}
