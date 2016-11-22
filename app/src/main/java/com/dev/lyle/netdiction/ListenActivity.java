package com.dev.lyle.netdiction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.lyle.netdiction.network.NetDictionConnection;

public class ListenActivity extends AppCompatActivity {
    private Button start_btn;
    private Button stop_btn;
    private Button cmd_btn;
    private TextView computer_name;
    private TextView address_port;
    private SpeechRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen2);

        final String address = getIntent().getStringExtra("address");
        final String name = getIntent().getStringExtra("name");
        final Integer port = Integer.parseInt(getIntent().getStringExtra("port"));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 0);
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this))
            Toast.makeText(this, "No speech recognizer available", Toast.LENGTH_LONG).show();

        final NetDictionRecognizer NDrec = new NetDictionRecognizer(getApplicationContext(), address, port);

        start_btn = (Button) findViewById(R.id.start_dictating);
        stop_btn = (Button) findViewById(R.id.stop_dictating);
        cmd_btn = (Button) findViewById(R.id.command);
        computer_name = (TextView) findViewById(R.id.name);
        address_port = (TextView) findViewById(R.id.address);

        computer_name.setText(name);
        address_port.setText(address + ":" + port);

        start_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NDrec.startRecognizer(NetDictionRecognizer.CONTINUOUS);
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NDrec.stopRecognizer();
            }
        });

        cmd_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NDrec.startRecognizer(NetDictionRecognizer.COMMAND);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
        }
    }
}
