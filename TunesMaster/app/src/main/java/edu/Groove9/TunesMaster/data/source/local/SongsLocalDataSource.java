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

import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class SongsLocalDataSource implements SongsDataSource {

    private static SongsLocalDataSource INSTANCE;

    private SongsDbHelper mDbHelper;

    // Prevent direct instantiation.
    private SongsLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new SongsDbHelper(context);
    }

    public static SongsLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SongsLocalDataSource(context);
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadSongsCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getSongs(@NonNull LoadSongsCallback callback) {
        List<Song> songs = new ArrayList<Song>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_SOURCE,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };

        Cursor c = db.query(
                SongsPersistenceContract.TaskEntry.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
                Uri source = Uri.parse(c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_SOURCE)));
                String title = c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
                String description =
                        c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
                Song song = new Song(title, description, itemId, source);
                songs.add(song);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (songs.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onSongsLoaded(songs);
        }

    }

    /**
     * Note: {@link GetSongCallback#onDataNotAvailable()} is fired if the {@link Song} isn't
     * found.
     */
    @Override
    public void getSong(@NonNull String id, @NonNull GetSongCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_SOURCE,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                SongsPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };

        String selection = SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {id};

        Cursor c = db.query(
                SongsPersistenceContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Song song = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID));
            Uri source = Uri.parse(c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_SOURCE)));
            String title = c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(SongsPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION));
            song = new Song(title, description, itemId, source);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (song != null) {
            callback.onSongLoaded(song);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveSong(@NonNull Song song) {
        checkNotNull(song);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID, song.getId());
        values.put(SongsPersistenceContract.TaskEntry.COLUMN_NAME_SOURCE, song.getSource().toString());
        values.put(SongsPersistenceContract.TaskEntry.COLUMN_NAME_TITLE, song.getTitle());
        values.put(SongsPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION, song.getDescription());

        db.insert(SongsPersistenceContract.TaskEntry.TABLE_NAME, null, values);

        db.close();
    }

    @Override
    public void refreshSongs() {
        // Not required because the {@link SongsRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllSongs() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(SongsPersistenceContract.TaskEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteSong(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = SongsPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        db.delete(SongsPersistenceContract.TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
