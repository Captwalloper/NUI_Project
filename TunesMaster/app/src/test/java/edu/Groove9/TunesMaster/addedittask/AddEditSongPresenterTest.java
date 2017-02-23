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

package edu.Groove9.TunesMaster.addedittask;

import android.net.Uri;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.Groove9.TunesMaster.TestUseCaseScheduler;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.SaveTask;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.data.source.SongsRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the implementation of {@link AddEditTaskPresenter}.
 */
public class AddEditSongPresenterTest {

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    @Mock
    private SongsRepository mSongsRepository;

    @Mock
    private AddEditTaskContract.View mAddEditTaskView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<SongsDataSource.GetSongCallback> mGetTaskCallbackCaptor;

    private AddEditTaskPresenter mAddEditTaskPresenter;

    @Before
    public void setupMocksAndView() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // The presenter wont't update the view unless it's active.
        when(mAddEditTaskView.isActive()).thenReturn(true);
    }

    @Test
    public void saveNewTaskToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mAddEditTaskPresenter = givenEditTaskPresenter("1");

        // When the presenter is asked to save a task
        mAddEditTaskPresenter.saveTask("New Song Title", "Some Song Description", SOURCE);

        // Then a task is saved in the repository and the view updated
        verify(mSongsRepository).saveSong(any(Song.class)); // saved to the model
        verify(mAddEditTaskView).showTasksList(); // shown in the UI
    }


    @Test
    public void saveTask_emptyTaskShowsErrorUi() {
        // Get a reference to the class under test
        mAddEditTaskPresenter = givenEditTaskPresenter(null);

        // When the presenter is asked to save an empty task
        mAddEditTaskPresenter.saveTask("", "", SOURCE);

        // Then an empty not error is shown in the UI
        verify(mAddEditTaskView).showEmptyTaskError();
    }

    @Test
    public void saveExistingTaskToRepository_showsSuccessMessageUi() {
        // Get a reference to the class under test
        mAddEditTaskPresenter = givenEditTaskPresenter("1");

        // When the presenter is asked to save an existing task
        mAddEditTaskPresenter.saveTask("New Song Title", "Some Song Description", SOURCE);

        // Then a task is saved in the repository and the view updated
        verify(mSongsRepository).saveSong(any(Song.class)); // saved to the model
        verify(mAddEditTaskView).showTasksList(); // shown in the UI
    }

    @Test
    public void populateTask_callsRepoAndUpdatesView() {
        Song testSong = new Song("TITLE", "DESCRIPTION", SOURCE);
        // Get a reference to the class under test
        mAddEditTaskPresenter = givenEditTaskPresenter(testSong.getId());

        // When the presenter is asked to populate an existing task
        mAddEditTaskPresenter.populateTask();

        // Then the task repository is queried and the view updated
        verify(mSongsRepository).getSong(eq(testSong.getId()), mGetTaskCallbackCaptor.capture());

        // Simulate callback
        mGetTaskCallbackCaptor.getValue().onSongLoaded(testSong);

        verify(mAddEditTaskView).setTitle(testSong.getTitle());
        verify(mAddEditTaskView).setDescription(testSong.getDescription());
    }

    private AddEditTaskPresenter givenEditTaskPresenter(String taskId) {

        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTask getTask = new GetTask(mSongsRepository);
        SaveTask saveTask = new SaveTask(mSongsRepository);

        return new AddEditTaskPresenter(useCaseHandler, taskId, mAddEditTaskView, getTask,
                saveTask);
    }
}
