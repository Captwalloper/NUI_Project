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

import edu.Groove9.TunesMaster.BasePresenter;
import edu.Groove9.TunesMaster.BaseView;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.SongStatus;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SongPlayerContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showMissingSong();

        void hideTitle();

        void showTitle(String title);

        void hideDescription();

        void showDescription(String description);

        void showEditSong(String taskId);

        void showNextSongFeedback();

        void showLastSongFeedback();

        void showPlaypauseFeedback();

        void showShuffleFeedback();

        void showSongProgress(int percent);

        void updatePlayPauseIcon(SongStatus songStatus);

    }

    interface Presenter extends BasePresenter {
        void editSong();

        void shuffleSong();

        void lastSong();

        void playPauseSong();

        void nextSong();

        void volumeUp();

        void volumeDown();

        void repeatSong();

        void updateProgress(AudioPlayerContract audioPlayer);

        void updatePlayPauseIcon(AudioPlayerContract audioPlayer);
    }

    interface Voice {
        void OnPlay();
        void OnPause();
        void OnNext();
        void OnLast();
        void OnShuffle();

        void OnFailure();
    }
}
