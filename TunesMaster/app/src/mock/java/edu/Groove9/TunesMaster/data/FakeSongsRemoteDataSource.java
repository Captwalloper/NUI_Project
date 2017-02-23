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

package edu.Groove9.TunesMaster.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeSongsRemoteDataSource implements SongsDataSource {

    private static FakeSongsRemoteDataSource INSTANCE;

    private static final Map<String, Song> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeSongsRemoteDataSource() {}

    public static FakeSongsRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeSongsRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getSongs(@NonNull LoadSongsCallback callback) {
        callback.onSongsLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void getSong(@NonNull String id, @NonNull GetSongCallback callback) {
        Song song = TASKS_SERVICE_DATA.get(id);
        callback.onSongLoaded(song);
    }

    @Override
    public void saveSong(@NonNull Song song) {
        TASKS_SERVICE_DATA.put(song.getId(), song);
    }

    public void refreshSongs() {
        // Not required because the {@link SongsRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteSong(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllSongs() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Song... songs) {
        for (Song song : songs) {
            TASKS_SERVICE_DATA.put(song.getId(), song);
        }
    }
}
