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

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class SongsRepository implements SongsDataSource {

    private static SongsRepository INSTANCE = null;

    private final SongsDataSource mTasksRemoteDataSource;

    private final SongsDataSource mTasksLocalDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Song> mCachedSongs;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    /**
     * Enables/Disables use of remote repository
     */
    private static final boolean mUseRemoteRepository = false;

    // Prevent direct instantiation.
    private SongsRepository(@NonNull SongsDataSource tasksRemoteDataSource,
                            @NonNull SongsDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksRemoteDataSource the backend data source
     * @param tasksLocalDataSource  the device storage data source
     * @return the {@link SongsRepository} instance
     */
    public static SongsRepository getInstance(SongsDataSource tasksRemoteDataSource,
                                              SongsDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SongsRepository(tasksRemoteDataSource, tasksLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(SongsDataSource, SongsDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadSongsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getSongs(@NonNull final LoadSongsCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedSongs != null && !mCacheIsDirty) {
            callback.onSongsLoaded(new ArrayList<>(mCachedSongs.values()));
            return;
        }

        if (mCacheIsDirty && mUseRemoteRepository) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            mTasksLocalDataSource.getSongs(new LoadSongsCallback() {
                @Override
                public void onSongsLoaded(List<Song> songs) {
                    refreshCache(songs);
                    callback.onSongsLoaded(new ArrayList<>(mCachedSongs.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void saveSong(@NonNull Song song) {
        checkNotNull(song);
        mTasksRemoteDataSource.saveSong(song);
        mTasksLocalDataSource.saveSong(song);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedSongs == null) {
            mCachedSongs = new LinkedHashMap<>();
        }
        mCachedSongs.put(song.getId(), song);
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link LoadSongsCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getSong(@NonNull final String id, @NonNull final GetSongCallback callback) {
        checkNotNull(id);
        checkNotNull(callback);

        Song cachedSong = getTaskWithId(id);

        // Respond immediately with cache if available
        if (cachedSong != null) {
            callback.onSongLoaded(cachedSong);
            return;
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mTasksLocalDataSource.getSong(id, new GetSongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                callback.onSongLoaded(song);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getSong(id, new GetSongCallback() {
                    @Override
                    public void onSongLoaded(Song song) {
                        callback.onSongLoaded(song);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void refreshSongs() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllSongs() {
        mTasksRemoteDataSource.deleteAllSongs();
        mTasksLocalDataSource.deleteAllSongs();

        if (mCachedSongs == null) {
            mCachedSongs = new LinkedHashMap<>();
        }
        mCachedSongs.clear();
    }

    @Override
    public void deleteSong(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteSong(checkNotNull(taskId));
        mTasksLocalDataSource.deleteSong(checkNotNull(taskId));

        mCachedSongs.remove(taskId);
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadSongsCallback callback) {
        mTasksRemoteDataSource.getSongs(new LoadSongsCallback() {
            @Override
            public void onSongsLoaded(List<Song> songs) {
                refreshCache(songs);
                refreshLocalDataSource(songs);
                callback.onSongsLoaded(new ArrayList<>(mCachedSongs.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Song> songs) {
        if (mCachedSongs == null) {
            mCachedSongs = new LinkedHashMap<>();
        }
        mCachedSongs.clear();
        for (Song song : songs) {
            mCachedSongs.put(song.getId(), song);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Song> songs) {
        mTasksLocalDataSource.deleteAllSongs();
        for (Song song : songs) {
            mTasksLocalDataSource.saveSong(song);
        }
    }

    @Nullable
    private Song getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedSongs == null || mCachedSongs.isEmpty()) {
            return null;
        } else {
            return mCachedSongs.get(id);
        }
    }
}
