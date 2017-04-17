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

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.StringTokenizer;

import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class PrototypeSongsLocalDataSource implements SongsDataSource {

    private static final String music_folder = "music";
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
     * Note: {@link LoadSongsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getSongs(@NonNull LoadSongsCallback callback) {
        List<Song> songs = loadSongsFromAssetsFolder();
        if (songs.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onSongsLoaded(songs);
        }

    }

    private List<Song> loadSongsFromAssetsFolder() {
        List<Song> songs = new ArrayList<Song>();

        String[] assetFiles;
        try {
            assetFiles = context.getAssets().list(music_folder);
            for (String file : assetFiles) {
                Song song = getSongFromFile(file);
                songs.add(song);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }

        return songs;
    }

    private static Song getSongFromFile(String filename) {
        String title = convertFilenameToTitle(filename);
        String description = "A test song";
        String id = filename;
        Uri source = Uri.parse(filename);
        return new Song(title, description, id, source);
    }

    public static Song getSongFromId(String id) {
        String filename = id;
        return getSongFromFile(filename);
    }

    /**
     * Note: {@link GetSongCallback#onDataNotAvailable()} is fired if the {@link Song} isn't
     * found.
     */
    @Override
    public void getSong(@NonNull String id, @NonNull GetSongCallback callback) {
        Song song = getSongFromId(id);
        if (song != null) {
            callback.onSongLoaded(song);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveSong(@NonNull Song song) {
        // do nothing
    }

    @Override
    public void refreshSongs() {
        // Not required because the {@link SongsRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllSongs() {
        // do nothing
    }

    @Override
    public void deleteSong(@NonNull String taskId) {
        // do nothing
    }

    private static String convertFilenameToTitle(String filename){
        String title = filename.substring(0, filename.lastIndexOf("."));
        title = title.replace("Cant", "Can't");
        return title;
    }
}
