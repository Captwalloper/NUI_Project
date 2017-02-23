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

package edu.Groove9.TunesMaster.statistics;

import android.net.Uri;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.Groove9.TunesMaster.TestUseCaseScheduler;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.statistics.domain.usecase.GetStatistics;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

/**
 * Unit tests for the implementation of {@link StatisticsPresenter}
 */
public class StatisticsPresenterTest {

    private static List<Song> TASKS;

    private static final Uri SOURCE = Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0");

    @Mock
    private SongsRepository mSongsRepository;

    @Mock
    private StatisticsContract.View mStatisticsView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<SongsDataSource.LoadSongsCallback> mLoadTasksCallbackCaptor;


    private StatisticsPresenter mStatisticsPresenter;

    @Before
    public void setupStatisticsPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mStatisticsPresenter = givenStatisticsPresenter();

        // The presenter won't update the view unless it's active.
        when(mStatisticsView.isActive()).thenReturn(true);

        // We start the tasks to 3, with one active and two completed
        TASKS = Lists.newArrayList(new Song("Title1", "Description1", SOURCE),
                new Song("Title2", "Description2", true, SOURCE), new Song("Title3", "Description3", true, SOURCE));
    }

    @Test
    public void loadEmptyTasksFromRepository_CallViewToDisplay() {
        // Given an initialized StatisticsPresenter with no tasks
        TASKS.clear();

        // When loading of Tasks is requested
        mStatisticsPresenter.start();

        //Then progress indicator is shown
        verify(mStatisticsView).setProgressIndicator(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        // Then progress indicator is hidden and correct data is passed on to the view
        verify(mStatisticsView).setProgressIndicator(false);
        verify(mStatisticsView).showStatistics(0, 0);
    }

    @Test
    public void loadNonEmptyTasksFromRepository_CallViewToDisplay() {
        // Given an initialized StatisticsPresenter with 1 active and 2 completed tasks

        // When loading of Tasks is requested
        mStatisticsPresenter.start();

        //Then progress indicator is shown
        verify(mStatisticsView).setProgressIndicator(true);

        // Callback is captured and invoked with stubbed tasks
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onSongsLoaded(TASKS);

        // Then progress indicator is hidden and correct data is passed on to the view
        verify(mStatisticsView).setProgressIndicator(false);
        verify(mStatisticsView).showStatistics(1, 2);
    }

    @Test
    public void loadStatisticsWhenTasksAreUnavailable_CallErrorToDisplay() {
        // When statistics are loaded
        mStatisticsPresenter.start();

        // And tasks data isn't available
        verify(mSongsRepository).getSongs(mLoadTasksCallbackCaptor.capture());
        mLoadTasksCallbackCaptor.getValue().onDataNotAvailable();

        // Then an error message is shown
        verify(mStatisticsView).showLoadingStatisticsError();
    }

    private StatisticsPresenter givenStatisticsPresenter() {
        UseCaseHandler useCaseHandler = new UseCaseHandler(new TestUseCaseScheduler());
        GetStatistics getStatistics = new GetStatistics(mSongsRepository);

        return new StatisticsPresenter(useCaseHandler, mStatisticsView, getStatistics);
    }
}
