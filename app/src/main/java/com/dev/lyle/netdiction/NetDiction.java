package com.dev.lyle.netdiction;
import java.util.ArrayList;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.lyle.netdiction.database.Computer;
import com.dev.lyle.netdiction.database.NetDictionDatabase;
import com.dev.lyle.netdiction.network.NetDictionConnection;
import com.dev.lyle.netdiction.network.ReplyListener;

public class NetDiction extends Activity {
    protected static final int RESULT_SPEECH = 1;
    private EditText address;
    private EditText port;
    private Button start;
    private ListView known_computers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_diction);

        address = (EditText) findViewById(R.id.address);
        port = (EditText) findViewById(R.id.port);
        start = (Button) findViewById(R.id.start);
        known_computers = (ListView) findViewById(R.id.known_computers);

        final ArrayList<Computer> computers = new NetDictionDatabase(getApplicationContext()).getAllKnownComputers();
        ArrayAdapter<Computer> adapter = new ArrayAdapter<Computer>(this, R.layout.simple_list_view, computers);
        known_computers.setAdapter(adapter);

        known_computers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Computer computer = computers.get(position);
                address.setText(computer.getAddress());
                port.setText(String.valueOf(computer.getPort()));
            }
        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReplyListener(getApplicationContext(), address.getText().toString(), Integer.parseInt(port.getText().toString())).start();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_net_diction, menu);
        return true;
    }
}