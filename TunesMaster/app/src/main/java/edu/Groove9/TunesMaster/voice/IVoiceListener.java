package edu.Groove9.TunesMaster.voice;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;

/**
 * Created by ConnorM on 3/23/2017.
 */

public interface IVoiceListener {
    final int REQ_CODE_SPEECH_INPUT = 100;

    void retrieveInputFromUser(Fragment fragment);
    VoiceResult getResult(int resultCode, Intent data);
}
