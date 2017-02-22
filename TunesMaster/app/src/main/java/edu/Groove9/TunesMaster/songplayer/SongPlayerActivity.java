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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.Groove9.TunesMaster.Injection;
import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.data.source.local.PrototypeSongsLocalDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.util.ActivityUtils;

/**
 * Displays task details screen.
 */
public class SongPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_SONG_ID = "SONG_ID";

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

        // Get the requested task id
        String songId = getIntent().getStringExtra(EXTRA_SONG_ID);

        SongPlayerFragment songPlayerFragment = (SongPlayerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (songPlayerFragment == null) {
            songPlayerFragment = SongPlayerFragment.newInstance(songId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    songPlayerFragment, R.id.contentFrame);
        }

        // Create the presenter
        Song song = PrototypeSongsLocalDataSource.getSongFromId(songId); //HACK
        new SongPlayerPresenter(
                Injection.provideUseCaseHandler(),
                song,
                songPlayerFragment,
                Injection.provideGetTask(getApplicationContext()),
                Injection.provideCompleteTasks(getApplicationContext()),
                Injection.provideActivateTask(getApplicationContext()),
                Injection.provideDeleteTask(getApplicationContext()),
                Injection.providePlayPauseSong(getApplicationContext()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}