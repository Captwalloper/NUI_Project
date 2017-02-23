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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Context;
import android.net.Uri;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
public class SongsRepositoryTest {

    private static final String TASK_TITLE = "title";

    private static final String TASK_TITLE2 = "title2";

    private static final String TASK_TITLE3 = "title3";

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    private static List<Song> TASKS = Lists.newArrayList(new Song("Title1", "Description1", SOURCE),
            new Song("Title2", "Description2", SOURCE));

    private SongsRepository mSongsRepository;

    @Mock
    private SongsDataSource mTasksRemoteDataSource;

    @Mock
    private SongsDataSource mTasksLocalDataSource;

    @Mock
    private Context mContext;

    @Mock
    private SongsDataSource.GetSongCallback mGetSongCallback;

    @Mock
    private SongsDataSource.LoadSongsCallback mLoadSongsCallback;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<SongsDataSource.LoadSongsCallback> mTasksCallbackCaptor;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<SongsDataSource.GetSongCallback> mTaskCallbackCaptor;

    @Before
    public void setupTasksRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mSongsRepository = SongsRepository.getInstance(
                mTasksRemoteDataSource, mTasksLocalDataSource);
    }

    @After
    public void destroyRepositoryInstance() {
        SongsRepository.destroyInstance();
    }

    @Test
    public void getTasks_repositoryCachesAfterFirstApiCall() {
        // Given a setup Captor to capture callbacks
        // When two calls are issued to the tasks repository
        twoTasksLoadCallsToRepository(mLoadSongsCallback);

        // Then tasks were only requested once from Service API
        verify(mTasksRemoteDataSource).getSongs(any(SongsDataSource.LoadSongsCallback.class));
    }

    @Test
    public void getTasks_requestsAllTasksFromLocalDataSource() {
        // When tasks are requested from the tasks repository
        mSongsRepository.getSongs(mLoadSongsCallback);

        // Then tasks are loaded from the local data source
        verify(mTasksLocalDataSource).getSongs(any(SongsDataSource.LoadSongsCallback.class));
    }

    @Test
    public void saveTask_savesTaskToServiceAPI() {
        // Given a stub task with title and description
        Song newSong = new Song(TASK_TITLE, "Some Song Description", SOURCE);

        // When a task is saved to the tasks repository
        mSongsRepository.saveSong(newSong);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).saveSong(newSong);
        verify(mTasksLocalDataSource).saveSong(newSong);
        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
    }

    @Test
    public void completeTask_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", SOURCE);
        mSongsRepository.saveSong(newSong);

        // When a task is completed to the tasks repository
    //    mSongsRepository.completeTask(newSong);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).completeTask(newSong);
        verify(mTasksLocalDataSource).completeTask(newSong);
        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
        assertThat(mSongsRepository.mCachedSongs.get(newSong.getId()).isActive(), is(false));
    }

    @Test
    public void completeTaskId_completesTaskToServiceAPIUpdatesCache() {
        // Given a stub active task with title and description added in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", SOURCE);
        mSongsRepository.saveSong(newSong);

        // When a task is completed using its id to the tasks repository
        mSongsRepository.completeTask(newSong.getId());

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).completeTask(newSong);
        verify(mTasksLocalDataSource).completeTask(newSong);
        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
        assertThat(mSongsRepository.mCachedSongs.get(newSong.getId()).isActive(), is(false));
    }

    @Test
    public void activateTask_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong);

        // When a completed task is activated to the tasks repository
        mSongsRepository.activateTask(newSong);

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).activateTask(newSong);
        verify(mTasksLocalDataSource).activateTask(newSong);
        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
        assertThat(mSongsRepository.mCachedSongs.get(newSong.getId()).isActive(), is(true));
    }

    @Test
    public void activateTaskId_activatesTaskToServiceAPIUpdatesCache() {
        // Given a stub completed task with title and description in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong);

        // When a completed task is activated with its id to the tasks repository
        mSongsRepository.activateTask(newSong.getId());

        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).activateTask(newSong);
        verify(mTasksLocalDataSource).activateTask(newSong);
        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
        assertThat(mSongsRepository.mCachedSongs.get(newSong.getId()).isActive(), is(true));
    }

    @Test
    public void getTask_requestsSingleTaskFromLocalDataSource() {
        // When a task is requested from the tasks repository
        mSongsRepository.getSong(TASK_TITLE, mGetSongCallback);

        // Then the task is loaded from the database
        verify(mTasksLocalDataSource).getSong(eq(TASK_TITLE), any(
                SongsDataSource.GetSongCallback.class));
    }

    @Test
    public void deleteCompletedTasks_deleteCompletedTasksToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong);
        Song newSong2 = new Song(TASK_TITLE2, "Some Song Description", SOURCE);
        mSongsRepository.saveSong(newSong2);
        Song newSong3 = new Song(TASK_TITLE3, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong3);

        // When a completed tasks are cleared to the tasks repository
        mSongsRepository.clearCompletedTasks();


        // Then the service API and persistent repository are called and the cache is updated
        verify(mTasksRemoteDataSource).clearCompletedTasks();
        verify(mTasksLocalDataSource).clearCompletedTasks();

        assertThat(mSongsRepository.mCachedSongs.size(), is(1));
        assertTrue(mSongsRepository.mCachedSongs.get(newSong2.getId()).isActive());
        assertThat(mSongsRepository.mCachedSongs.get(newSong2.getId()).getTitle(), is(TASK_TITLE2));
    }

    @Test
    public void deleteAllTasks_deleteTasksToServiceAPIUpdatesCache() {
        // Given 2 stub completed tasks and 1 stub active tasks in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong);
        Song newSong2 = new Song(TASK_TITLE2, "Some Song Description", SOURCE);
        mSongsRepository.saveSong(newSong2);
        Song newSong3 = new Song(TASK_TITLE3, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong3);

        // When all tasks are deleted to the tasks repository
        mSongsRepository.deleteAllSongs();

        // Verify the data sources were called
        verify(mTasksRemoteDataSource).deleteAllSongs();
        verify(mTasksLocalDataSource).deleteAllSongs();

        assertThat(mSongsRepository.mCachedSongs.size(), is(0));
    }

    @Test
    public void deleteTask_deleteTaskToServiceAPIRemovedFromCache() {
        // Given a task in the repository
        Song newSong = new Song(TASK_TITLE, "Some Song Description", true, SOURCE);
        mSongsRepository.saveSong(newSong);
        assertThat(mSongsRepository.mCachedSongs.containsKey(newSong.getId()), is(true));

        // When deleted
        mSongsRepository.deleteSong(newSong.getId());

        // Verify the data sources were called
        verify(mTasksRemoteDataSource).deleteSong(newSong.getId());
        verify(mTasksLocalDataSource).deleteSong(newSong.getId());

        // Verify it's removed from repository
        assertThat(mSongsRepository.mCachedSongs.containsKey(newSong.getId()), is(false));
    }

    @Test
    public void getTasksWithDirtyCache_tasksAreRetrievedFromRemote() {
        // When calling getSongs in the repository with dirty cache
        mSongsRepository.refreshSongs();
        mSongsRepository.getSongs(mLoadSongsCallback);

        // And the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // Verify the tasks from the remote data source are returned, not the local
        verify(mTasksLocalDataSource, never()).getSongs(mLoadSongsCallback);
        verify(mLoadSongsCallback).onSongsLoaded(TASKS);
    }

    @Test
    public void getTasksWithLocalDataSourceUnavailable_tasksAreRetrievedFromRemote() {
        // When calling getSongs in the repository
        mSongsRepository.getSongs(mLoadSongsCallback);

        // And the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource);

        // And the remote data source has data available
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // Verify the tasks from the local data source are returned
        verify(mLoadSongsCallback).onSongsLoaded(TASKS);
    }

    @Test
    public void getTasksWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // When calling getSongs in the repository
        mSongsRepository.getSongs(mLoadSongsCallback);

        // And the local data source has no data available
        setTasksNotAvailable(mTasksLocalDataSource);

        // And the remote data source has no data available
        setTasksNotAvailable(mTasksRemoteDataSource);

        // Verify no data is returned
        verify(mLoadSongsCallback).onDataNotAvailable();
    }

    @Test
    public void getTaskWithBothDataSourcesUnavailable_firesOnDataUnavailable() {
        // Given a task id
        final String taskId = "123";

        // When calling getSong in the repository
        mSongsRepository.getSong(taskId, mGetSongCallback);

        // And the local data source has no data available
        setTaskNotAvailable(mTasksLocalDataSource, taskId);

        // And the remote data source has no data available
        setTaskNotAvailable(mTasksRemoteDataSource, taskId);

        // Verify no data is returned
        verify(mGetSongCallback).onDataNotAvailable();
    }

    @Test
    public void getTasks_refreshesLocalDataSource() {
        // Mark cache as dirty to force a reload of data from remote data source.
        mSongsRepository.refreshSongs();

        // When calling getSongs in the repository
        mSongsRepository.getSongs(mLoadSongsCallback);

        // Make the remote data source return data
        setTasksAvailable(mTasksRemoteDataSource, TASKS);

        // Verify that the data fetched from the remote data source was saved in local.
        verify(mTasksLocalDataSource, times(TASKS.size())).saveSong(any(Song.class));
    }

    /**
     * Convenience method that issues two calls to the tasks repository
     */
    private void twoTasksLoadCallsToRepository(SongsDataSource.LoadSongsCallback callback) {
        // When tasks are requested from repository
        mSongsRepository.getSongs(callback); // First call to API

        // Use the Mockito Captor to capture the callback
        verify(mTasksLocalDataSource).getSongs(mTasksCallbackCaptor.capture());

        // Local data source doesn't have data yet
        mTasksCallbackCaptor.getValue().onDataNotAvailable();


        // Verify the remote data source is queried
        verify(mTasksRemoteDataSource).getSongs(mTasksCallbackCaptor.capture());

        // Trigger callback so tasks are cached
        mTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        mSongsRepository.getSongs(callback); // Second call to API
    }

    private void setTasksNotAvailable(SongsDataSource dataSource) {
        verify(dataSource).getSongs(mTasksCallbackCaptor.capture());
        mTasksCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTasksAvailable(SongsDataSource dataSource, List<Song> songs) {
        verify(dataSource).getSongs(mTasksCallbackCaptor.capture());
        mTasksCallbackCaptor.getValue().onSongsLoaded(songs);
    }

    private void setTaskNotAvailable(SongsDataSource dataSource, String taskId) {
        verify(dataSource).getSong(eq(taskId), mTaskCallbackCaptor.capture());
        mTaskCallbackCaptor.getValue().onDataNotAvailable();
    }

    private void setTaskAvailable(SongsDataSource dataSource, Song song) {
        verify(dataSource).getSong(eq(song.getId()), mTaskCallbackCaptor.capture());
        mTaskCallbackCaptor.getValue().onSongLoaded(song);
    }
 }
