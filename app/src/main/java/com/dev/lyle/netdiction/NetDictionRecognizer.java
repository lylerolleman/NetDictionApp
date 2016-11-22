package com.dev.lyle.netdiction;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import com.dev.lyle.netdiction.network.NetDictionConnection;

import java.util.ArrayList;

/**
 * Created by Lyle on 6/27/2016.
 */
public class NetDictionRecognizer implements RecognitionListener {
    private String address;
    private Integer port;
    private Context context;
    private SpeechRecognizer recognizer;
    private boolean cont;
    private AudioManager am;
    private int transmitted;
    private int mode;

    public static final int CONTINUOUS = 1;
    public static final int COMMAND = 2;

    public NetDictionRecognizer(Context context, String address, Integer port) {
        this.address = address;
        this.port = port;
        this.context = context;
        this.recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        this.recognizer.setRecognitionListener(this);
        this.cont = true;
        this.mode = CONTINUOUS;
        this.am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.transmitted = 0;
        this.mode = 0;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
        Toast.makeText(context, "Ready for speech", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("STATUS", "end of speech");

    }

    @Override
    public void onError(int error) {
        String error_toast = "Something went wrong " + error;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                error_toast += ": ERROR_AUDIO";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                error_toast += ": ERROR_CLIENT";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                error_toast += ": ERROR_INSUFFICIENT_PERMISSIONS";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                error_toast += ": ERROR_NETWORK";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                error_toast += ": ERROR_NETWORK_TIMEOUT";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                error_toast += ": ERROR_NO_MATCH";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                error_toast += ": ERROR_RECOGNIZER_BUSY";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                error_toast += ": ERROR_SERVER";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                error_toast += ": ERROR_SPEECH_TIMEOUT";
                break;
        }
        Toast.makeText(context, error_toast, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResults(Bundle results) {
        String text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        Log.d("result", text);
        if (text.length() > transmitted) {
            NetDictionConnection con = new NetDictionConnection(address, port);
            con.start();
            if (mode == COMMAND) {
                Log.d("transmit", text.substring(transmitted));
                con.sendCommandMessage(text.substring(transmitted));
            } else
                con.sendTypeMessage(text.substring(transmitted));
            con.closeConnection();
        }
        transmitted = 0;
        //resetRecognizer();
        if (cont)
            startRecognizer(mode);
        else
            Toast.makeText(context, "Speech recognition ended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        try {
            String text = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
            NetDictionConnection con = new NetDictionConnection(address, port);
            con.start();
            if (mode != COMMAND) {
                con.sendTypeMessage(text.substring(transmitted));
                transmitted = text.length();
            }
            con.closeConnection();

        } catch (Exception e) {
            Toast.makeText(context, "Partial results triggered with error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    public void startRecognizer(int mode) {
        this.mode = mode;
        if (mode != CONTINUOUS)
            cont = false;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        recognizer.startListening(intent);
    }

    public void resetRecognizer() {
        recognizer.destroy();
        recognizer = SpeechRecognizer.createSpeechRecognizer(context);
        recognizer.setRecognitionListener(this);
    }

    public void stopRecognizer() {
        Log.d("STATUS", "Recognizer stopped");
        cont = false;
        am.setStreamMute(AudioManager.STREAM_MUSIC, false);
        recognizer.stopListening();
    }
}
