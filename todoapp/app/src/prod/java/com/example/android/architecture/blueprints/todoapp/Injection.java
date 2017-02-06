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

package com.Groove9.TunesMaster;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.support.annotation.NonNull;

import com.Groove9.TunesMaster.addedittask.domain.usecase.DeleteTask;
import com.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import com.Groove9.TunesMaster.addedittask.domain.usecase.SaveTask;
import com.Groove9.TunesMaster.data.source.TasksDataSource;
import com.Groove9.TunesMaster.data.source.TasksRepository;
import com.Groove9.TunesMaster.data.source.local.TasksLocalDataSource;
import com.Groove9.TunesMaster.data.source.remote.TasksRemoteDataSource;
import com.Groove9.TunesMaster.statistics.domain.usecase.GetStatistics;
import com.Groove9.TunesMaster.tasks.domain.filter.FilterFactory;
import com.Groove9.TunesMaster.tasks.domain.usecase.ActivateTask;
import com.Groove9.TunesMaster.tasks.domain.usecase.ClearCompleteTasks;
import com.Groove9.TunesMaster.tasks.domain.usecase.CompleteTask;
import com.Groove9.TunesMaster.tasks.domain.usecase.GetTasks;

/**
 * Enables injection of production implementations for
 * {@link TasksDataSource} at compile time.
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context));
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideTasksRepository(context), new FilterFactory());
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetTask provideGetTask(Context context) {
        return new GetTask(Injection.provideTasksRepository(context));
    }

    public static SaveTask provideSaveTask(Context context) {
        return new SaveTask(Injection.provideTasksRepository(context));
    }

    public static CompleteTask provideCompleteTasks(Context context) {
        return new CompleteTask(Injection.provideTasksRepository(context));
    }

    public static ActivateTask provideActivateTask(Context context) {
        return new ActivateTask(Injection.provideTasksRepository(context));
    }

    public static ClearCompleteTasks provideClearCompleteTasks(Context context) {
        return new ClearCompleteTasks(Injection.provideTasksRepository(context));
    }

    public static DeleteTask provideDeleteTask(Context context) {
        return new DeleteTask(Injection.provideTasksRepository(context));
    }

    public static GetStatistics provideGetStatistics(Context context) {
        return new GetStatistics(Injection.provideTasksRepository(context));
    }
}
