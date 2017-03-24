/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.Groove9.TunesMaster.songplayer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.addedittask.AddEditTaskActivity;
import edu.Groove9.TunesMaster.addedittask.AddEditTaskFragment;
import edu.Groove9.TunesMaster.voice.IVoiceListener;
import edu.Groove9.TunesMaster.voice.IVoiceRecognizer;
import edu.Groove9.TunesMaster.voice.VoiceListener;
import edu.Groove9.TunesMaster.voice.VoiceRecognizer;
import edu.Groove9.TunesMaster.voice.VoiceResult;

import static android.app.Activity.RESULT_OK;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the task detail screen.
 */
public class SongPlayerFragment extends Fragment implements SongPlayerContract.View {

    @NonNull
    private static final String ARGUMENT_TASK_ID = "TASK_ID";

    @NonNull
    private static final int REQUEST_EDIT_TASK = 1;

    private SongPlayerContract.Presenter mPresenter;

    private TextView mDetailTitle;

    private TextView mDetailDescription;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // Use for Voice
    IVoiceListener voiceListener;
    IVoiceRecognizer voiceRecognizer;

    public static SongPlayerFragment newInstance(@Nullable String taskId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_ID, taskId);
        SongPlayerFragment fragment = new SongPlayerFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);

        mPresenter.start();
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.songplayer_frag, container, false);
        setHasOptionsMenu(true);
        mDetailTitle = (TextView) root.findViewById(R.id.task_detail_title);
        mDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);

        //Set up floating action button
//        FloatingActionButton fab =
//                (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.editSong();
//            }
//        });

        setupSongPanel(root);
        setupShakeDetector();
        setupVoice();
        setupControlPanel(root);
        setupMacroPanel(root);

        return root;
    }

    private void setupSongPanel(View root) {
        //Set up song panel (gestures)
        LinearLayout songPanel = (LinearLayout) root.findViewById(R.id.song_player_song_panel);
        songPanel.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(),"top",Toast.LENGTH_SHORT).show();
                mPresenter.volumeUp();
            }
            public void onSwipeRight() {
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
                mPresenter.nextSong();
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                mPresenter.lastSong();
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
                mPresenter.volumeDown();
            }
            public void onSingleTap() {
                Toast.makeText(getActivity(), "single tap", Toast.LENGTH_SHORT).show();
                mPresenter.playPauseSong();
            }
        });
    }

    private void setupShakeDetector() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                mPresenter.shuffleSong();
            }
        });
    }

    private void setupControlPanel(View root) {
        Button shuffle = (Button) root.findViewById(R.id.song_player_shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.shuffleSong();
            }
        });
        Button last = (Button) root.findViewById(R.id.song_player_last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.lastSong();
            }
        });
        Button playpause = (Button) root.findViewById(R.id.song_player_playpause);
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playPauseSong();
            }
        });
        Button next = (Button) root.findViewById(R.id.song_player_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.nextSong();
            }
        });
        Button replay=(Button)root.findViewById(R.id.song_player_replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.repeatSong();
            }
        });
    }

    private void setupVoice() {
        voiceListener = new VoiceListener();
        voiceRecognizer = new VoiceRecognizer();
    }

    private void setupMacroPanel(View root) {
        View macroPanel = root.findViewById(R.id.macro_panel);
        Button mic = (Button) macroPanel.findViewById(R.id.mic_btn);
        final Fragment frag = this;
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceListener.retrieveInputFromUser(frag);
            }
        });
    }

    @Override
    public void setPresenter(@NonNull SongPlayerContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteSong();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mDetailTitle.setText("");
            mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(@NonNull String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showEditSong(@NonNull String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showSongDeleted() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_EDIT_TASK:
                // If the task was edited successfully, go back to the list.
                if (resultCode == RESULT_OK) {
                    getActivity().finish();
                }
                break;
            case IVoiceListener.REQ_CODE_SPEECH_INPUT:
                VoiceResult result = voiceListener.getResult(resultCode, data);
                if (result.isSuccess()) {
                    Map<String, Runnable> commands = new HashMap<>();
                    commands.put("play", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.playPauseSong();
                        }
                    });
                    commands.put("pause|paws", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.playPauseSong();
                        }
                    });
                    commands.put("next", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.nextSong();
                        }
                    });
                    commands.put("last", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.lastSong();
                        }
                    });
                    commands.put("shuffle", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.shuffleSong();
                        }
                    });
                    String voiceText = result.getValue();
                    showMessage(voiceText);
                    Runnable action = voiceRecognizer.determineAction(voiceText, commands);
                    action.run();
                } else {
                    showError(result.getValue());
                }
                break;
        }
    }

    public void showError(String error) {
        Toast.makeText(getActivity().getApplicationContext(),
                error,
                Toast.LENGTH_SHORT).show();
    }

    public void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(),
                message,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTitle(@NonNull String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void showMissingSong() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }

}
