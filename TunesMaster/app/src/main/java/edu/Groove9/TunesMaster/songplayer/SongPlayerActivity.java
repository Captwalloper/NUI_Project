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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import edu.Groove9.TunesMaster.Injection;
import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.help.HelpActivity;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.util.ActivityUtils;

import static java.security.AccessController.getContext;

/**
 * Displays task details screen.
 */
public class SongPlayerActivity extends AppCompatActivity {

//    public static final String PLAYLIST_KEY = "PLAYLIST_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.songplayer_act);
        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        // Get the playlist
        Playlist playlist = (Playlist)getIntent().getSerializableExtra("edu.Groove9.TunesMaster.playlist.domain.model.Playlist");

        SongPlayerFragment songPlayerFragment = (SongPlayerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (songPlayerFragment == null) {
            songPlayerFragment = SongPlayerFragment.newInstance(playlist.getCurrentSong().getId());

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    songPlayerFragment, R.id.contentFrame);
        }

        // Create the presenter
        new SongPlayerPresenter(
                Injection.provideUseCaseHandler(),
                playlist,
                songPlayerFragment,
                Injection.provideGetTask(getApplicationContext()),
                Injection.providePlayPauseSong(getApplicationContext()),
                Injection.provideNextSong(getApplicationContext()),
                Injection.provideLastSong(getApplicationContext()),
                Injection.provideShuffleSong(getApplicationContext()),
                Injection.provideVolumeUp(getApplicationContext()),
                Injection.provideVolumeDown(getApplicationContext()),
                Injection.provideRepeatSong(getApplicationContext())
                );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                showHelp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showHelp() {
        Intent intent = new Intent(this, HelpActivity.class);
        final String helpText = new StringBuilder()
                .append("CONTROL GUIDE\n")
                .append("\n")
                .append("Gestures (perform on/around song title)\n")
                .append("  *Play/Pause     ->  Single Tap\n")
                .append("  *Next Song      ->  Swipe Left\n")
                .append("  *Last Song      ->  Swipe Right\n")
                .append("  *Volume Up      ->  Swipe Up\n")
                .append("  *Volume Down    ->  Swipe Down\n")
                .append("  *Shuffle        ->  Shake the device!\n")
                .append("\n")
                .append("Speech (press mic button)\n")
                .append("  *Play           ->  “Play”\n")
                .append("  *Pause          ->  “Pause””\n")
                .append("  *Next Song      ->  “Next”\n")
                .append("  *Last Song      ->  “Last” or “Previous”\n")
                .append("  *Shuffle        ->  “Shuffle”\n")
                .append("\n")
                .append("Touch\n")
                .append("  *Play/Pause     ->  Center button”\n")
                .append("  *Next Song      ->  2nd button from left”\n")
                .append("  *Last Song      ->  4th button from left”\n")
                .append("  *Shuffle        ->  Rightmost button”\n")
                .append("  *Replay         ->  Leftmost button”\n")
                .append("  *Song Position  ->  Bar below buttons;\n" +
                        "                      touch/drag to adjust\n")
                .append("  *Mic            ->  Bottom; press for speech\n")
                .append("\n")
                .toString();
        intent.putExtra("Help_Text", helpText);
        startActivity(intent);
    }
}
