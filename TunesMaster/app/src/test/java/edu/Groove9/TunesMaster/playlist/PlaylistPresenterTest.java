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

package edu.Groove9.TunesMaster.playlist;

import android.net.Uri;

import edu.Groove9.TunesMaster.TestUseCaseScheduler;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.data.source.SongsDataSource.LoadSongsCallback;
import edu.Groove9.TunesMaster.playlist.domain.filter.FilterFactory;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.playlist.domain.usecase.GetTasks;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the implementation of {@link PlaylistPresenter}
 */
public class PlaylistPresenterTest {

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    private static List<Song> TASKS;

    @Mock
    private SongsRepository mSongsRepository;

    @Mock
    private PlaylistContract.View mTasksView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<LoadSongsCallback> mLoadTasksCallbackCaptor;

    private PlaylistPresenter mPlaylistPresenter;

    @Before
    public void setupTasksPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mPlaylistPresenter = givenTasksPresenter();

        // The presenter won't update the view unless it's active.
        when(mTasksView.isActive()).thenReturn(true);

        // We start the tasks to 3, with one active and two completed
        TASKS = Lists.newArrayList(new Song("Title1", "Description1", SOURCE),
                new Song("Title2", "Description2", true, SOURCE), new Song("Title3", "Description3", true, SOURCE));
    }

    private PlaylistPresenter givenTasksPresenter() {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTasks getTasks = new GetTasks(mSongsRepository, new FilterFactory());

        return new PlaylistPresenter(useCaseHandler, mTasksView, getTasks);
    }

    @Test
    public void loadAllTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized PlaylistPresenter with initialized tasks
        // When loading of Tasks is requested
        mPlaylistPresenter.setFiltering(PlaylistFilterType.ALL_TASKS);
        mPlaylistPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        // Then progress indicator is shown
        InOrder inOrder = inOrder(mTasksView);
        inOrder.verify(mTasksView).setLoadingIndicator(true);
        // Then progress indicator is hidden and all tasks are shown in UI
        inOrder.verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 3);
    }

    @Test
    public void loadActiveTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized PlaylistPresenter with initialized tasks
        // When loading of Tasks is requested
        mPlaylistPresenter.setFiltering(PlaylistFilterType.ACTIVE_TASKS);
        mPlaylistPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        // Then progress indicator is hidden and active tasks are shown in UI
        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 1);
    }

    @Test
    public void loadCompletedTasksFromRepositoryAndLoadIntoView() {
        // Given an initialized PlaylistPresenter with initialized tasks
        // When loading of Tasks is requested
        mPlaylistPresenter.setFiltering(PlaylistFilterType.COMPLETED_TASKS);
        mPlaylistPresenter.loadTasks(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        // Then progress indicator is hidden and completed tasks are shown in UI
        verify(mTasksView).setLoadingIndicator(false);
        ArgumentCaptor<List> showTasksArgumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(mTasksView).showTasks(showTasksArgumentCaptor.capture());
        assertTrue(showTasksArgumentCaptor.getValue().size() == 2);
    }

    @Test
    public void clickOnFab_ShowsAddTaskUi() {
        // When adding a new task
        mPlaylistPresenter.addNewTask();

        // Then add task UI is shown
        verify(mTasksView).showAddTask();
    }

    @Test
    public void clickOnTask_ShowsDetailUi() {
        // Given a stubbed active task
        Song requestedSong = new Song("Details Requested", "For this task", SOURCE);

        // When open task details is requested
        mPlaylistPresenter.openSongPlayer(requestedSong);

        // Then task detail UI is shown
        verify(mTasksView).showSongPlayerUI(any(String.class));
    }

    @Test
    public void unavailableTasks_ShowsError() {
        // When tasks are loaded
        mPlaylistPresenter.setFiltering(PlaylistFilterType.ALL_TASKS);
        mPlaylistPresenter.loadTasks(true);

        // And the tasks aren't available in the repository
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        verify(mTasksView).showLoadingTasksError();
    }
}
