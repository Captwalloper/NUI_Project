package edu.Groove9.TunesMaster.voice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import edu.Groove9.TunesMaster.R;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ConnorM on 3/23/2017.
 */

public class VoiceListener implements IVoiceListener {
    private String failureMessage;
    private static final int REQUEST_MICROPHONE = 13;

    private void ensurePermission(Fragment fragment) {
        if (ContextCompat.checkSelfPermission(fragment.getActivity(),
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(fragment.getActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        }
    }

    @Override
    public void retrieveInputFromUser(Fragment fragment) {
        ensurePermission(fragment);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                fragment.getString(R.string.speech_prompt));
        try {
            fragment.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            setFailureMessage(fragment.getString(R.string.speech_not_supported));
        }
    }

    public VoiceResult getResult(int resultCode, Intent data) {
        if (hasFailureMessage()) {
            String message = popErrorMessage();
            return new VoiceResult(false, message);
        }
        else if (resultCode == RESULT_OK && null != data) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String value = result.get(0);
            return new VoiceResult(true, value);
        }
        else {
            throw new RuntimeException("VoiceListener failed to return an expected result.");
        }
    }

    private boolean hasFailureMessage() {
        return failureMessage != null;
    }

    private String popErrorMessage() {
        String message = failureMessage;
        failureMessage = null;
        return message;
    }

    private void setFailureMessage(String message) {
        failureMessage = message;
    }


}
