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

import edu.Groove9.TunesMaster.data.source.TasksDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;

    private static final Map<String, Song> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    // Prevent direct instantiation.
    private FakeTasksRemoteDataSource() {}

    public static FakeTasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        Song song = TASKS_SERVICE_DATA.get(taskId);
        callback.onTaskLoaded(song);
    }

    @Override
    public void saveTask(@NonNull Song song) {
        TASKS_SERVICE_DATA.put(song.getId(), song);
    }

    @Override
    public void completeTask(@NonNull Song song) {
        Song completedSong = new Song(song.getTitle(), song.getDescription(), song.getId(), true, song.getSource());
        TASKS_SERVICE_DATA.put(song.getId(), completedSong);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

    @Override
    public void activateTask(@NonNull Song song) {
        Song activeSong = new Song(song.getTitle(), song.getDescription(), song.getId(), song.getSource());
        TASKS_SERVICE_DATA.put(song.getId(), activeSong);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        // Not required for the remote data source.
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Song>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Song> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    public void refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @VisibleForTesting
    public void addTasks(Song... songs) {
        for (Song song : songs) {
            TASKS_SERVICE_DATA.put(song.getId(), song);
        }
    }
}
