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

package edu.Groove9.TunesMaster.data.source;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import java.util.List;

/**
 * Main entry point for accessing tasks data.
 * <p>
 * For simplicity, only getSongs() and getSong() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For Groove9, when a new task is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface SongsDataSource {

    interface LoadSongsCallback {

        void onSongsLoaded(List<Song> songs);

        void onDataNotAvailable();
    }

    interface GetSongCallback {

        void onSongLoaded(Song song);

        void onDataNotAvailable();
    }

    void getSongs(@NonNull LoadSongsCallback callback);

    void getSong(@NonNull String id, @NonNull GetSongCallback callback);

    void saveSong(@NonNull Song song);

    void refreshSongs();

    void deleteAllSongs();

    void deleteSong(@NonNull String taskId);
}
