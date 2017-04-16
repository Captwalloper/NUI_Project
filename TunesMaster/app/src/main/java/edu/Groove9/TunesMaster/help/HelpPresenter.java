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

package edu.Groove9.TunesMaster.help;

import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.songplayer.SongPlayerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link SongPlayerFragment}), retrieves the data and updates
 * the UI as required.
 */
public class HelpPresenter implements HelpContract.Presenter{
    private final UseCaseHandler mUseCaseHandler;
    private final String helpText;
    private final HelpContract.View mHelpView;

    public HelpPresenter(UseCaseHandler mUseCaseHandler, String helpText, HelpContract.View helpView) {
        this.mUseCaseHandler = mUseCaseHandler;
        this.helpText = helpText;
        this.mHelpView = helpView;
        // showHelp usecase
    }

    @Override
    public void help() {
        mHelpView.showHelpDescription(helpText);
    }

    @Override
    public void start() {
        help();
    }
}
