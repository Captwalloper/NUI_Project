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

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.statistics.domain.usecase.GetStatistics;
import edu.Groove9.TunesMaster.statistics.domain.model.Statistics;

/**
 * Listens to user actions from the UI ({@link StatisticsFragment}), retrieves the data and updates
 * the UI as required.
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final StatisticsContract.View mStatisticsView;
    private final UseCaseHandler mUseCaseHandler;
    private final GetStatistics mGetStatistics;

    public StatisticsPresenter(
            @NonNull UseCaseHandler useCaseHandler,
            @NonNull StatisticsContract.View statisticsView,
            @NonNull GetStatistics getStatistics) {
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mStatisticsView = checkNotNull(statisticsView, "StatisticsView cannot be null!");
        mGetStatistics = checkNotNull(getStatistics,"getStatistics cannot be null!");

        mStatisticsView.setPresenter(this);
    }

    @Override
    public void start() {

    }

}
