package com.visionless.cinemareservation;

public class Connection {
    static String host;
    static int port;

    public Connection(String host, int port){
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
