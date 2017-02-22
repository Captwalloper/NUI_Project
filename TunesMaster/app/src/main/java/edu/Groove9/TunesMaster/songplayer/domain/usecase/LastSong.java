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

package edu.Groove9.TunesMaster.songplayer.domain.usecase;

import android.support.annotation.NonNull;

import java.util.List;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.data.source.TasksDataSource;
import edu.Groove9.TunesMaster.data.source.TasksRepository;
import edu.Groove9.TunesMaster.playlist.PlaylistFilterType;
import edu.Groove9.TunesMaster.playlist.domain.filter.FilterFactory;
import edu.Groove9.TunesMaster.playlist.domain.filter.TaskFilter;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of tasks.
 */
public class LastSong extends UseCase<LastSong.RequestValues, LastSong.ResponseValue> {

    private final TasksRepository mTasksRepository;

    private final FilterFactory mFilterFactory;

    public LastSong(@NonNull TasksRepository tasksRepository, @NonNull FilterFactory filterFactory) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mFilterFactory = checkNotNull(filterFactory, "filterFactory cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        if (values.isForceUpdate()) {
            mTasksRepository.refreshTasks();
        }

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Song> songs) {
                PlaylistFilterType currentFiltering = values.getCurrentFiltering();
                TaskFilter taskFilter = mFilterFactory.create(currentFiltering);

                List<Song> tasksFiltered = taskFilter.filter(songs);
                ResponseValue responseValue = new ResponseValue(tasksFiltered);
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });

    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final PlaylistFilterType mCurrentFiltering;
        private final boolean mForceUpdate;

        public RequestValues(boolean forceUpdate, @NonNull PlaylistFilterType currentFiltering) {
            mForceUpdate = forceUpdate;
            mCurrentFiltering = checkNotNull(currentFiltering, "currentFiltering cannot be null!");
        }

        public boolean isForceUpdate() {
            return mForceUpdate;
        }

        public PlaylistFilterType getCurrentFiltering() {
            return mCurrentFiltering;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final List<Song> mSongs;

        public ResponseValue(@NonNull List<Song> songs) {
            mSongs = checkNotNull(songs, "songs cannot be null!");
        }

        public List<Song> getTasks() {
            return mSongs;
        }
    }
}
