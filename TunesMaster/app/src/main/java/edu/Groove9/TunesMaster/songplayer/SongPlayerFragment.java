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
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import edu.Groove9.TunesMaster.Injection;
import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.addedittask.AddEditTaskActivity;
import edu.Groove9.TunesMaster.addedittask.AddEditTaskFragment;
import edu.Groove9.TunesMaster.help.HelpActivity;
import edu.Groove9.TunesMaster.logging.Logger;
import edu.Groove9.TunesMaster.logging.UserEvent;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.voice.CommandParseException;
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

   // private TextView mDetailDescription;

    private TextView mDetailDescription;
    private Button playpause ;
    private Button last ;
    private Button next;
    private Button shuffle;
    private SeekBar mSongProgress;

    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    // Use for Voice
    IVoiceListener voiceListener;
    IVoiceRecognizer voiceRecognizer;

    private AudioPlayerContract audioPlayer;
    private Handler songProgressUpdateHandler;
    private Timer songProgressUpdateTimer;

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
       // mDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);

        playpause = (Button) root.findViewById(R.id.song_player_playpause);
        last = (Button) root.findViewById(R.id.song_player_last);
        next = (Button) root.findViewById(R.id.song_player_next);
        shuffle = (Button) root.findViewById(R.id.song_player_shuffle);

        audioPlayer = Injection.provideAudioPlayer(getActivity().getApplicationContext());

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

        setupSongProgressSeekbar(root);

        return root;
    }

    private void setupSongPanel(View root) {
        //Set up song panel (gestures)
        LinearLayout songPanel = (LinearLayout) root.findViewById(R.id.song_player_song_panel);
        songPanel.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(),"Increase Volume",Toast.LENGTH_SHORT).show();
                mPresenter.volumeUp();
            }
            public void onSwipeRight() {
                //last.setBackground(getResources().getDrawable(R.drawable.last_fill));
                showPreviousSongFeedback();
                Toast.makeText(getActivity(), "Previous Song", Toast.LENGTH_SHORT).show();
                mPresenter.nextSong();
                Logger.get().log(new UserEvent(UserEvent.Source.Gesture, UserEvent.Action.Next));
            }
            public void onSwipeLeft() {
                showNextSongFeedback();
                Toast.makeText(getActivity(), "Next Song", Toast.LENGTH_SHORT).show();
                mPresenter.lastSong();
                Logger.get().log(new UserEvent(UserEvent.Source.Gesture, UserEvent.Action.Last));
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "Decrease Volume", Toast.LENGTH_SHORT).show();
                mPresenter.volumeDown();
            }
            public void onSingleTap() {

                Toast.makeText(getActivity(), "Play", Toast.LENGTH_SHORT).show();
                mPresenter.playPauseSong();
                showPlaypauseFeedback();
                Logger.get().log(new UserEvent(UserEvent.Source.Gesture, UserEvent.Action.PlayPause));
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
                Logger.get().log(new UserEvent(UserEvent.Source.Gesture, UserEvent.Action.Shuffle));
            }
        });
    }

    private void setupControlPanel(View root) {
        Button shuffle = (Button) root.findViewById(R.id.song_player_shuffle);
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.shuffleSong();
                showShuffleFeedback();
                Logger.get().log(new UserEvent(UserEvent.Source.Touch, UserEvent.Action.Shuffle));
            }
        });
        Button last = (Button) root.findViewById(R.id.song_player_last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.lastSong();
                showPreviousSongFeedback();
                Logger.get().log(new UserEvent(UserEvent.Source.Touch, UserEvent.Action.Last));
            }
        });
        Button playpause = (Button) root.findViewById(R.id.song_player_playpause);
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.playPauseSong();
                showPlaypauseFeedback();
                Logger.get().log(new UserEvent(UserEvent.Source.Touch, UserEvent.Action.PlayPause));
            }
        });
        Button next = (Button) root.findViewById(R.id.song_player_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.nextSong();
                showNextSongFeedback();
                Logger.get().log(new UserEvent(UserEvent.Source.Touch, UserEvent.Action.Next));
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mDetailTitle.setText("");
            //mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public void hideDescription() {
        //mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(@NonNull String description) {
        //mDetailDescription.setVisibility(View.VISIBLE);
       // mDetailDescription.setText(description);
    }

    @Override
    public void showEditSong(@NonNull String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showNextSongFeedback() {
        showButtonFeedback(next, getResources().getDrawable(R.drawable.next_fill), getResources().getDrawable(R.drawable.next));
    }

    @Override
    public void showPreviousSongFeedback() {
        showButtonFeedback(last, getResources().getDrawable(R.drawable.last_fill), getResources().getDrawable(R.drawable.last));
    }

    @Override
    public void showPlaypauseFeedback() {
        boolean playing = playpause.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.play).getConstantState());
        if (playing) {
            playpause.setBackground(getResources().getDrawable(R.drawable.pause));
        } else {
            playpause.setBackground(getResources().getDrawable(R.drawable.play));
        }
    }


    @Override
    public void showShuffleFeedback() {
        showButtonFeedback(shuffle, getResources().getDrawable(R.drawable.shuffle_filled), getResources().getDrawable(R.drawable.shuffle));
    }


    public void showButtonFeedback(Button button, Drawable pressed, Drawable normal){
        button.setBackground(pressed);

        final Button but = button;
        final Drawable norm = normal;
        final int delay = 1000;
        new CountDownTimer(delay, delay) {
            public void onTick(long millisUntilFinished) {/*ignore*/}

            public void onFinish() {
                but.setBackground(norm);
            }

        }.start();
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
                            Logger.get().log(new UserEvent(UserEvent.Source.Voice, UserEvent.Action.PlayPause));
                        }
                    });
                    commands.put("pause|paws", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.playPauseSong();
                            Logger.get().log(new UserEvent(UserEvent.Source.Voice, UserEvent.Action.PlayPause));
                        }
                    });
                    commands.put("next", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.nextSong();
                            Logger.get().log(new UserEvent(UserEvent.Source.Voice, UserEvent.Action.Next));
                        }
                    });
                    commands.put("last", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.lastSong();
                            Logger.get().log(new UserEvent(UserEvent.Source.Voice, UserEvent.Action.Last));
                        }
                    });
                    commands.put("shuffle", new Runnable() {
                        @Override
                        public void run() {
                            mPresenter.shuffleSong();
                            Logger.get().log(new UserEvent(UserEvent.Source.Voice, UserEvent.Action.Shuffle));
                        }
                    });
                    String voiceText = result.getValue();
                    Runnable action = null;
                    try {
                        action = voiceRecognizer.determineAction(voiceText, commands);
                        String commandName = voiceRecognizer.determineCommandName(voiceText, commands);
                        showMessage("Input: " + voiceText
                                    + "\n" + "Command: " + commandName);
                        action.run();
                    } catch (CommandParseException e) {
                        showError(e.getMessage());
                    }
                } else {
                    showError(result.getValue());
                }
                break;
        }
    }

    public void showError(String error) {
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.RED);
        toast.show();
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
        //mDetailDescription.setText(getString(R.string.no_data));
    }

    @Override
    public void showSongProgress(int percent) {
        mSongProgress.setProgress(percent);
    }

    private void setupSongProgressSeekbar(View root) {
        mSongProgress = (SeekBar) root.findViewById(R.id.song_player_progress);
        mSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioPlayer.setPercentProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {  }
        });

        setupSongProgressUpdateHandler();
        songProgressUpdateTimer = new Timer();
        startSongProgressTimer();
    }

    private void setupSongProgressUpdateHandler() {
        songProgressUpdateHandler = new Handler() {
            public void handleMessage(Message msg) {
                mPresenter.updateProgress(audioPlayer);
            }
        };
    }

    private void startSongProgressTimer() {
        songProgressUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                songProgressUpdateHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 1000);
    }

}
