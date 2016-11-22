package com.dev.lyle.netdiction.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Lyle on 6/15/2016.
 */
public class NetDictionConnection extends Thread {
    private Socket socket;
    private String address;
    private int port;
    private LinkedList<String> nextmessage;
    private BufferedWriter writer;

    public NetDictionConnection(String address, int port) {
        this.address = address;
        this.port = port;
        this.nextmessage = new LinkedList();
    }

    public void run() {
        synchronized (this) {
            try {
                socket = new Socket(address, port);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                while (true) {
                    if (nextmessage.size() == 0) {
                        wait();
                    }
                    String mes = nextmessage.pop();
                    send(mes);
                    if (mes.startsWith("DISCONNECT")) {
                        writer.close();
                        socket.close();
                        return;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void send(String command) {
        try {
            writer.write(command);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        synchronized (this) {
            nextmessage.add("DISCONNECT;\r\n");
            notify();
        }
    }

    public void sendTypeMessage(String mes) {
        synchronized (this) {
            Log.d("DEBUGMES", mes);
            nextmessage.add("TYPE \"" + mes + "\";\r\n");
            notify();
        }
    }

    public void sendCommandMessage(String mes) {
        synchronized (this) {
            nextmessage.add("COMMAND \"" + mes + "\";\r\n");
            notify();
        }
    }

    public void sendPing() {
        synchronized (this) {
            nextmessage.add("PING;\r\n");
            notify();
        }
    }
}
