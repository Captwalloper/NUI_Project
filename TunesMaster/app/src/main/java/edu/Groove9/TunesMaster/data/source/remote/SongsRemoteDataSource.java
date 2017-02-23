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

package edu.Groove9.TunesMaster.data.source.remote;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class SongsRemoteDataSource implements SongsDataSource {

    private static SongsRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private static final Map<String, Song> TASKS_SERVICE_DATA;

    static {
        TASKS_SERVICE_DATA = new LinkedHashMap<>(2);
        addTask("One Punch Man Theme", "1st song on OST.", Uri.parse("https://www.youtube.com/watch?v=E8XaV1yjabk"));
        addTask("Transistor OST", "Full soundtrack to the game Transistor.", Uri.parse("https://www.youtube.com/watch?v=-zA1jRmAYfU&t"));
    }

    public static SongsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SongsRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private SongsRemoteDataSource() {}

    private static void addTask(String title, String description, Uri source) {
        Song newSong = new Song(title, description, source);
        TASKS_SERVICE_DATA.put(newSong.getId(), newSong);
    }

    /**
     * Note: {@link LoadSongsCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getSongs(final @NonNull LoadSongsCallback callback) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSongsLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    /**
     * Note: {@link GetSongCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getSong(@NonNull String id, final @NonNull GetSongCallback callback) {
        final Song song = TASKS_SERVICE_DATA.get(id);

        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onSongLoaded(song);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveSong(@NonNull Song song) {
        TASKS_SERVICE_DATA.put(song.getId(), song);
    }

    @Override
    public void refreshSongs() {
        // Not required because the {@link SongsRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllSongs() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteSong(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
