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

import static com.google.common.base.Preconditions.checkNotNull;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.SaveTask;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

/**
 * Listens to user actions from the UI ({@link AddEditTaskFragment}), retrieves the data and
 * updates
 * the UI as required.
 */
public class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    private final AddEditTaskContract.View mAddTaskView;

    private final GetTask mGetTask;

    private final SaveTask mSaveTask;

    private final UseCaseHandler mUseCaseHandler;

    @Nullable
    private String mTaskId;

    /**
     * Creates a presenter for the add/edit view.
     *
     * @param taskId      ID of the task to edit or null for a new task
     * @param addTaskView the add/edit view
     */
    public AddEditTaskPresenter(@NonNull UseCaseHandler useCaseHandler, @Nullable String taskId,
            @NonNull AddEditTaskContract.View addTaskView, @NonNull GetTask getTask,
            @NonNull SaveTask saveTask) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mTaskId = taskId;
        mAddTaskView = checkNotNull(addTaskView, "addTaskView cannot be null!");
        mGetTask = checkNotNull(getTask, "getSong cannot be null!");
        mSaveTask = checkNotNull(saveTask, "saveSong cannot be null!");

        mAddTaskView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mTaskId != null) {
            populateTask();
        }
    }

    @Override
    public void saveTask(String title, String description, Uri source) {
        if (isNewTask()) {
            createTask(title, description, source);
        } else {
            updateTask(title, description, source);
        }
    }

    @Override
    public void populateTask() {
        if (mTaskId == null) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }

        mUseCaseHandler.execute(mGetTask, new GetTask.RequestValues(mTaskId),
                new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTask.ResponseValue response) {
                        showTask(response.getTask());
                    }

                    @Override
                    public void onError() {
                        showEmptyTaskError();
                    }
                });
    }

    private void showTask(Song song) {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.setTitle(song.getTitle());
            mAddTaskView.setDescription(song.getDescription());
            mAddTaskView.setSource(song.getSource());
        }
    }

    private void showSaveError() {
        // Show error, log, etc.
    }

    private void showEmptyTaskError() {
        // The view may not be able to handle UI updates anymore
        if (mAddTaskView.isActive()) {
            mAddTaskView.showEmptyTaskError();
        }
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description, Uri source) {
        Song newSong = new Song(title, description, source);
        if (newSong.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        } else {
            mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(newSong),
                    new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                        @Override
                        public void onSuccess(SaveTask.ResponseValue response) {
                            mAddTaskView.showTasksList();
                        }

                        @Override
                        public void onError() {
                            showSaveError();
                        }
                    });
        }
    }

    private void updateTask(String title, String description, Uri source) {
        if (mTaskId == null) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        Song newSong = new Song(title, description, mTaskId, source);
        mUseCaseHandler.execute(mSaveTask, new SaveTask.RequestValues(newSong),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {
                        // After an edit, go back to the list.
                        mAddTaskView.showTasksList();
                    }

                    @Override
                    public void onError() {
                        showSaveError();
                    }
                });
    }
}
