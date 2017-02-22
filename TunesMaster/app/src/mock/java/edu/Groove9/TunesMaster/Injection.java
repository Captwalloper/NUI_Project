/*
 * Copyright (C) 2015 The Android Open Source Project
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

package edu.Groove9.TunesMaster;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.addedittask.domain.usecase.DeleteTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.SaveTask;
import edu.Groove9.TunesMaster.data.FakeTasksRemoteDataSource;
import edu.Groove9.TunesMaster.data.source.TasksDataSource;
import edu.Groove9.TunesMaster.data.source.TasksRepository;
import edu.Groove9.TunesMaster.data.source.local.PrototypeSongsLocalDataSource;
import edu.Groove9.TunesMaster.data.source.local.TasksLocalDataSource;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.PlayPauseSong;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.PrototypeAudioPlayer;
import edu.Groove9.TunesMaster.statistics.domain.usecase.GetStatistics;
import edu.Groove9.TunesMaster.playlist.domain.filter.FilterFactory;
import edu.Groove9.TunesMaster.playlist.domain.usecase.ActivateTask;
import edu.Groove9.TunesMaster.playlist.domain.usecase.ClearCompleteTasks;
import edu.Groove9.TunesMaster.playlist.domain.usecase.CompleteTask;
import edu.Groove9.TunesMaster.playlist.domain.usecase.GetTasks;

/**
 * Enables injection of mock implementations for
 * {@link TasksDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
                PrototypeSongsLocalDataSource.getInstance(context));
    }


    public static AudioPlayerContract provideAudioPlayer(@NonNull Context context) {
        checkNotNull(context);
        return PrototypeAudioPlayer.getInstance(context);
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideTasksRepository(context), new FilterFactory());
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetTask provideGetTask(@NonNull Context context) {
        return new GetTask(Injection.provideTasksRepository(context));
    }

    public static SaveTask provideSaveTask(@NonNull Context context) {
        return new SaveTask(Injection.provideTasksRepository(context));
    }

    public static CompleteTask provideCompleteTasks(@NonNull Context context) {
        return new CompleteTask(Injection.provideTasksRepository(context));
    }

    public static ActivateTask provideActivateTask(@NonNull Context context) {
        return new ActivateTask(Injection.provideTasksRepository(context));
    }

    public static ClearCompleteTasks provideClearCompleteTasks(@NonNull Context context) {
        return new ClearCompleteTasks(Injection.provideTasksRepository(context));
    }

    public static DeleteTask provideDeleteTask(@NonNull Context context) {
        return new DeleteTask(Injection.provideTasksRepository(context));
    }

    public static GetStatistics provideGetStatistics(@NonNull Context context) {
        return new GetStatistics(Injection.provideTasksRepository(context));
    }

    public static PlayPauseSong providePlayPauseSong(@NonNull Context context) {
        return new PlayPauseSong(Injection.provideAudioPlayer(context));
    }
}
