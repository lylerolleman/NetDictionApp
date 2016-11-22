package com.dev.lyle.netdiction.database;

/**
 * Created by Lyle on 6/27/2016.
 */
public class Computer {
    private String name;
    private String address;
    private int port;

    public Computer(String name, String address, int port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return name + ", " + address + ":" + port;
    }
}
