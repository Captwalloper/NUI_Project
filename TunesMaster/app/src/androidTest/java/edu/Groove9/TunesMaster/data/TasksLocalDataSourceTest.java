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

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.data.source.local.SongsDbHelper;
import edu.Groove9.TunesMaster.data.source.local.SongsLocalDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Integration test for the {@link SongsDataSource}, which uses the {@link SongsDbHelper}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TasksLocalDataSourceTest {

    private static final String TITLE = "title";

    private static final String TITLE2 = "title2";

    private static final String TITLE3 = "title3";

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    private SongsLocalDataSource mLocalDataSource;

    @Before
    public void setup() {
         mLocalDataSource = SongsLocalDataSource.getInstance(
                 InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
        mLocalDataSource.deleteAllSongs();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalDataSource);
    }

    @Test
    public void saveTask_retrievesTask() {
        // Given a new task
        final Song newSong = new Song(TITLE, "", SOURCE);

        // When saved into the persistent repository
        mLocalDataSource.saveSong(newSong);

        // Then the task can be retrieved from the persistent repository
        mLocalDataSource.getSong(newSong.getId(), new SongsDataSource.GetSongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                assertThat(song, is(newSong));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void completeTask_retrievedTaskIsComplete() {
        // Initialize mock for the callback.
        SongsDataSource.GetSongCallback callback = mock(SongsDataSource.GetSongCallback.class);
        // Given a new task in the persistent repository
        final Song newSong = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong);

        // When completed in the persistent repository
        mLocalDataSource.completeTask(newSong);

        // Then the task can be retrieved from the persistent repository and is complete
        mLocalDataSource.getSong(newSong.getId(), new SongsDataSource.GetSongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                assertThat(song, is(newSong));
                assertThat(song.isCompleted(), is(true));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void activateTask_retrievedTaskIsActive() {
        // Initialize mock for the callback.
        SongsDataSource.GetSongCallback callback = mock(SongsDataSource.GetSongCallback.class);

        // Given a new completed task in the persistent repository
        final Song newSong = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong);
        mLocalDataSource.completeTask(newSong);

        // When activated in the persistent repository
        mLocalDataSource.activateTask(newSong);

        // Then the task can be retrieved from the persistent repository and is active
        mLocalDataSource.getSong(newSong.getId(), callback);

        verify(callback, never()).onDataNotAvailable();
        verify(callback).onSongLoaded(newSong);

        assertThat(newSong.isCompleted(), is(false));
    }

    @Test
    public void clearCompletedTask_taskNotRetrievable() {
        // Initialize mocks for the callbacks.
        SongsDataSource.GetSongCallback callback1 = mock(SongsDataSource.GetSongCallback.class);
        SongsDataSource.GetSongCallback callback2 = mock(SongsDataSource.GetSongCallback.class);
        SongsDataSource.GetSongCallback callback3 = mock(SongsDataSource.GetSongCallback.class);

        // Given 2 new completed tasks and 1 active task in the persistent repository
        final Song newSong1 = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong1);
        mLocalDataSource.completeTask(newSong1);
        final Song newSong2 = new Song(TITLE2, "", SOURCE);
        mLocalDataSource.saveSong(newSong2);
        mLocalDataSource.completeTask(newSong2);
        final Song newSong3 = new Song(TITLE3, "", SOURCE);
        mLocalDataSource.saveSong(newSong3);

        // When completed tasks are cleared in the repository
        mLocalDataSource.clearCompletedTasks();

        // Then the completed tasks cannot be retrieved and the active one can
        mLocalDataSource.getSong(newSong1.getId(), callback1);

        verify(callback1).onDataNotAvailable();
        verify(callback1, never()).onSongLoaded(newSong1);

        mLocalDataSource.getSong(newSong2.getId(), callback2);

        verify(callback2).onDataNotAvailable();
        verify(callback2, never()).onSongLoaded(newSong1);

        mLocalDataSource.getSong(newSong3.getId(), callback3);

        verify(callback3, never()).onDataNotAvailable();
        verify(callback3).onSongLoaded(newSong3);
    }

    @Test
    public void deleteAllTasks_emptyListOfRetrievedTask() {
        // Given a new task in the persistent repository and a mocked callback
        Song newSong = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong);
        SongsDataSource.LoadSongsCallback callback = mock(SongsDataSource.LoadSongsCallback.class);

        // When all tasks are deleted
        mLocalDataSource.deleteAllSongs();

        // Then the retrieved tasks is an empty list
        mLocalDataSource.getSongs(callback);

        verify(callback).onDataNotAvailable();
        verify(callback, never()).onSongsLoaded(anyList());
    }

    @Test
    public void getTasks_retrieveSavedTasks() {
        // Given 2 new tasks in the persistent repository
        final Song newSong1 = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong1);
        final Song newSong2 = new Song(TITLE, "", SOURCE);
        mLocalDataSource.saveSong(newSong2);

        // Then the tasks can be retrieved from the persistent repository
        mLocalDataSource.getSongs(new SongsDataSource.LoadSongsCallback() {
            @Override
            public void onSongsLoaded(List<Song> songs) {
                assertNotNull(songs);
                assertTrue(songs.size() >= 2);

                boolean newTask1IdFound = false;
                boolean newTask2IdFound = false;
                for (Song song : songs) {
                    if (song.getId().equals(newSong1.getId())) {
                        newTask1IdFound = true;
                    }
                    if (song.getId().equals(newSong2.getId())) {
                        newTask2IdFound = true;
                    }
                }
                assertTrue(newTask1IdFound);
                assertTrue(newTask2IdFound);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }
}
