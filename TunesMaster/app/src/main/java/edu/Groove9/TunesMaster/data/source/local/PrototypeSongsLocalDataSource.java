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

package edu.Groove9.TunesMaster.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.Groove9.TunesMaster.data.source.TasksDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class PrototypeSongsLocalDataSource implements TasksDataSource {

    private static PrototypeSongsLocalDataSource INSTANCE;

    private Context context;

    // Prevent direct instantiation.
    private PrototypeSongsLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        this.context = context;
    }

    public static PrototypeSongsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PrototypeSongsLocalDataSource(context);
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadTasksCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        List<Song> songs = loadSongsFromAssetsFolder();
        if (songs.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(songs);
        }

    }

    private List<Song> loadSongsFromAssetsFolder() {
        List<Song> songs = new ArrayList<Song>();

        String[] assetFiles;
        try {
            assetFiles = context.getAssets().list("");
            for (String file : assetFiles) {
                Song song = getSongFromFile(file);
                songs.add(song);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }

        return songs;
    }

    private Song getSongFromFile(String filename) {
        String title = filename;
        String description = "A test song";
        String id = filename;
        boolean completed = false;
        Uri source = Uri.parse(filename);
        return new Song(title, description, id, completed, source);
    }

    private Song getSongFromId(String id) {
        String filename = id;
        return getSongFromFile(filename);
    }

    /**
     * Note: {@link GetTaskCallback#onDataNotAvailable()} is fired if the {@link Song} isn't
     * found.
     */
    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Song song = getSongFromId(taskId);
        if (song != null) {
            callback.onTaskLoaded(song);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(@NonNull Song song) {
        // do nothing
    }

    @Override
    public void completeTask(@NonNull Song song) {
        // do nothing
    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Song song) {
        // do nothing
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    @Override
    public void clearCompletedTasks() {
        // do nothing
    }

    @Override
    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllTasks() {
        // do nothing
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        // do nothing
    }
}
