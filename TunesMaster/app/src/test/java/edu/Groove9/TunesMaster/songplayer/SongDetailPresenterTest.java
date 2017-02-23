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

package edu.Groove9.TunesMaster.songplayer;

import android.net.Uri;

import edu.Groove9.TunesMaster.TestUseCaseScheduler;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.DeleteTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.PlayPauseSong;
import edu.Groove9.TunesMaster.songplayer.player.PrototypeAudioPlayer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link SongPlayerPresenter}
 */
public class SongDetailPresenterTest {

    public static final String TITLE_TEST = "title";

    public static final String DESCRIPTION_TEST = "description";

    public static final String INVALID_TASK_ID = "";

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    public static final Song ACTIVE_SONG = new Song(TITLE_TEST, DESCRIPTION_TEST, SOURCE);

    public static final Song COMPLETED_SONG = new Song(TITLE_TEST, DESCRIPTION_TEST, true, SOURCE);

    @Mock
    private SongsRepository mSongsRepository;

    @Mock
    private PrototypeAudioPlayer mAudioPlayer;

    @Mock
    private SongPlayerContract.View mTaskDetailView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<SongsDataSource.GetSongCallback> mGetTaskCallbackCaptor;

    private SongPlayerPresenter mSongPlayerPresenter;

    @Before
    public void setup() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // The presenter won't update the view unless it's active.
//        when(mTaskDetailView.isActive()).thenReturn(true);
    }

    @Test
    public void getActiveTaskFromRepositoryAndLoadIntoView() {
        // When tasks presenter is asked to open a task
        mSongPlayerPresenter = givenTaskDetailPresenter(ACTIVE_SONG.getId());
        mSongPlayerPresenter.start();

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(mSongsRepository).getSong(eq(ACTIVE_SONG.getId()), mGetTaskCallbackCaptor.capture());
        InOrder inOrder = inOrder(mTaskDetailView);
        inOrder.verify(mTaskDetailView).setLoadingIndicator(true);

        // When task is finally loaded
        mGetTaskCallbackCaptor.getValue().onSongLoaded(ACTIVE_SONG); // Trigger callback

        // Then progress indicator is hidden and title, description and completion status are shown
        // in UI
        inOrder.verify(mTaskDetailView).setLoadingIndicator(false);
        verify(mTaskDetailView).showTitle(TITLE_TEST);
        verify(mTaskDetailView).showDescription(DESCRIPTION_TEST);
    }

    @Test
    public void getCompletedTaskFromRepositoryAndLoadIntoView() {
        mSongPlayerPresenter = givenTaskDetailPresenter(COMPLETED_SONG.getId());
        mSongPlayerPresenter.start();

        // Then task is loaded from model, callback is captured and progress indicator is shown
        verify(mSongsRepository).getSong(
                eq(COMPLETED_SONG.getId()), mGetTaskCallbackCaptor.capture());
        InOrder inOrder = inOrder(mTaskDetailView);
        inOrder.verify(mTaskDetailView).setLoadingIndicator(true);

        // When task is finally loaded
        mGetTaskCallbackCaptor.getValue().onSongLoaded(COMPLETED_SONG); // Trigger callback

        // Then progress indicator is hidden and title, description and completion status are shown
        // in UI
        inOrder.verify(mTaskDetailView).setLoadingIndicator(false);
        verify(mTaskDetailView).showTitle(TITLE_TEST);
        verify(mTaskDetailView).showDescription(DESCRIPTION_TEST);
    }

    @Test
    public void getUnknownTaskFromRepositoryAndLoadIntoView() {
        // When loading of a task is requested with an invalid task ID.
        mSongPlayerPresenter = givenTaskDetailPresenter(INVALID_TASK_ID);
        mSongPlayerPresenter.start();
        verify(mTaskDetailView).showMissingSong();
    }

    @Test
    public void deleteTask() {
        // Given an initialized SongPlayerPresenter with stubbed song
        Song song = new Song(TITLE_TEST, DESCRIPTION_TEST, SOURCE);

        // When the deletion of a song is requested
        mSongPlayerPresenter = givenTaskDetailPresenter(song.getId());
        mSongPlayerPresenter.deleteSong();

        // Then the repository and the view are notified
        verify(mSongsRepository).deleteSong(song.getId());
        verify(mTaskDetailView).showSongDeleted();
    }



    @Test
    public void activeTaskIsShownWhenEditing() {
        // When the edit of an ACTIVE_SONG is requested
        mSongPlayerPresenter = givenTaskDetailPresenter(ACTIVE_SONG.getId());
        mSongPlayerPresenter.editSong();

        // Then the view is notified
        verify(mTaskDetailView).showEditSong(ACTIVE_SONG.getId());
    }

    @Test
    public void invalidTaskIsNotShownWhenEditing() {
        // When the edit of an invalid task id is requested
        mSongPlayerPresenter = givenTaskDetailPresenter(INVALID_TASK_ID);
        mSongPlayerPresenter.editSong();

        // Then the edit mode is never started
        verify(mTaskDetailView, never()).showEditSong(INVALID_TASK_ID);
        // instead, the error is shown.
        verify(mTaskDetailView).showMissingSong();
    }

    private SongPlayerPresenter givenTaskDetailPresenter(String id) {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetTask getTask = new GetTask(mSongsRepository);
        DeleteTask deleteTask = new DeleteTask(mSongsRepository);
        PlayPauseSong playPauseSong = new PlayPauseSong(mAudioPlayer);

        return new SongPlayerPresenter(useCaseHandler, new Song(id, "test", Uri.parse("")), mTaskDetailView,
                getTask, deleteTask, playPauseSong);
    }

}
