package com.dev.lyle.netdiction.network;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.dev.lyle.netdiction.ListenActivity;
import com.dev.lyle.netdiction.database.NetDictionDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Lyle on 7/4/2016.
 */
public class ReplyListener extends Thread {
    private String address;
    private int port;
    private Context context;

    public ReplyListener(Context context, String address, int port) {
        this.context = context;
        this.address = address;
        this.port = port;
    }

    public void run() {
        try {
            Socket socket = new Socket(address, port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.write("PING;\r\n");
            writer.flush();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("REPLY")) {
                    writer.write("DISCONNECT;\r\n");
                    writer.flush();
                    writer.close();
                    reader.close();
                    socket.close();
                    int i = line.indexOf(" ");
                    int j = line.lastIndexOf(";");
                    final String name = line.substring(i, j);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Intent i = new Intent(context, ListenActivity.class);
                            NetDictionDatabase db = new NetDictionDatabase(context);
                            if (!db.isKnownComputer(name, address, port))
                                db.addComputer(name, address, port);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("name", name);
                            i.putExtra("address", address);
                            i.putExtra("port", String.valueOf(port));
                            context.startActivity(i);
                        }
                    });
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}
