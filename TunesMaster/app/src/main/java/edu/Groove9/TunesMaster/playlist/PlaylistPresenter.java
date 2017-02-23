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

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.Activity;
import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.addedittask.AddEditTaskActivity;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.playlist.domain.usecase.GetTasks;

import java.util.List;

/**
 * Listens to user actions from the UI ({@link PlaylistFragment}), retrieves the data and updates the
 * UI as required.
 */
public class PlaylistPresenter implements PlaylistContract.Presenter {


    private final PlaylistContract.View mTasksView;
    private final GetTasks mGetTasks;

    private PlaylistFilterType mCurrentFiltering = PlaylistFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    private final UseCaseHandler mUseCaseHandler;

    public PlaylistPresenter(@NonNull UseCaseHandler useCaseHandler,
                             @NonNull PlaylistContract.View tasksView, @NonNull GetTasks getTasks) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "usecaseHandler cannot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mGetTasks = checkNotNull(getTasks, "getSong cannot be null!");

        mTasksView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode
                && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link SongsDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }

        GetTasks.RequestValues requestValue = new GetTasks.RequestValues(forceUpdate,
                mCurrentFiltering);

        mUseCaseHandler.execute(mGetTasks, requestValue,
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasks.ResponseValue response) {
                        List<Song> songs = response.getTasks();
                        // The view may not be able to handle UI updates anymore
                        if (!mTasksView.isActive()) {
                            return;
                        }
                        if (showLoadingUI) {
                            mTasksView.setLoadingIndicator(false);
                        }

                        processTasks(songs);
                    }

                    @Override
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        if (!mTasksView.isActive()) {
                            return;
                        }
                        mTasksView.showLoadingTasksError();
                    }
                });
    }

    private void processTasks(List<Song> songs) {
        if (songs.isEmpty()) {
            // Show a message indicating there are no songs for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of songs
            mTasksView.showTasks(songs);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openSongPlayer(@NonNull Playlist playlist) {
        checkNotNull(playlist, "playlist cannot be null!");
        mTasksView.showSongPlayerUI(playlist);
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be {@link PlaylistFilterType#ALL_TASKS},
     *                    {@link PlaylistFilterType#COMPLETED_TASKS}, or
     *                    {@link PlaylistFilterType#ACTIVE_TASKS}
     */
    @Override
    public void setFiltering(PlaylistFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public PlaylistFilterType getFiltering() {
        return mCurrentFiltering;
    }

}
