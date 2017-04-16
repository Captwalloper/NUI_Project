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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.UseCaseHandler;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.LastSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.NextSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.PlayPauseSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.RepeatSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.ShuffleSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.VolumeDown;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.VolumeUp;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.SongStatus;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link SongPlayerFragment}), retrieves the data and updates
 * the UI as required.
 */
public class SongPlayerPresenter implements SongPlayerContract.Presenter {

    private final SongPlayerContract.View mView;
    private final UseCaseHandler mUseCaseHandler;
    private final GetTask mGetTask;
    private final PlayPauseSong mPlayPauseSong;
    private final RepeatSong mRepeatSong;
    private final NextSong mNextSong;
    private final LastSong mLastSong;
    private final ShuffleSong mShuffleSong;
    private final VolumeUp mVolumeUp;
    private final VolumeDown mVolumeDown;

    @Nullable
    private Playlist mPlaylist;

    public SongPlayerPresenter(@NonNull UseCaseHandler useCaseHandler,
                               @Nullable Playlist playlist,
                               @NonNull SongPlayerContract.View taskDetailView,
                               @NonNull GetTask getTask,
                               @NonNull PlayPauseSong playPauseSong,
                               @NonNull NextSong nextSong,
                               @NonNull LastSong lastSong,
                               @NonNull ShuffleSong shuffleSong,
                               @NonNull VolumeUp volumeUp,
                               @NonNull VolumeDown volumeDown,
                               @NonNull RepeatSong repeatSong
                               ) {

        mPlaylist = playlist;
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mGetTask = checkNotNull(getTask, "getSong cannot be null!");
        mPlayPauseSong = playPauseSong;
        mNextSong = nextSong;
        mLastSong = lastSong;
        mShuffleSong = shuffleSong;
        mVolumeUp = volumeUp;
        mVolumeDown = volumeDown;
        mView.setPresenter(this);
        mRepeatSong = repeatSong;
    }



    @Override
    public void start() {
        openSong();
    }

    private void openSong() {
        if (Strings.isNullOrEmpty(mPlaylist.getCurrentSong().getId())) {
            mView.showMissingSong();
            return;
        }

        mView.setLoadingIndicator(true);

        mUseCaseHandler.execute(mGetTask, new GetTask.RequestValues(mPlaylist.getCurrentSong().getId()),
                new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTask.ResponseValue response) {
                        Song song = response.getTask();

                        // The view may not be able to handle UI updates anymore
                        mView.setLoadingIndicator(false);
                        showTask(song);
                    }

                    @Override
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        mView.showMissingSong();
                    }
                });
    }

    @Override
    public void editSong() {
        if (Strings.isNullOrEmpty(mPlaylist.getCurrentSong().getId())) {
            mView.showMissingSong();
            return;
        }
        mView.showEditSong(mPlaylist.getCurrentSong().getId());
    }

    @Override
    public void shuffleSong() {
        mUseCaseHandler.execute(mShuffleSong, new ShuffleSong.RequestValues(mPlaylist),
                new UseCase.UseCaseCallback<ShuffleSong.ResponseValue>() {
                    @Override
                    public void onSuccess(ShuffleSong.ResponseValue response) {
                        playPauseSong();
                        mView.showShuffleFeedback();
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    @Override
    public void lastSong() {
        mUseCaseHandler.execute(mLastSong, new LastSong.RequestValues(mPlaylist),
                new UseCase.UseCaseCallback<LastSong.ResponseValue>() {
                    @Override
                    public void onSuccess(LastSong.ResponseValue response) {
                        playPauseSong();
                        mView.showLastSongFeedback();
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    @Override
    public void playPauseSong() {
        mUseCaseHandler.execute(mPlayPauseSong, new PlayPauseSong.RequestValues(mPlaylist.getCurrentSong()),
                new UseCase.UseCaseCallback<PlayPauseSong.ResponseValue>() {
                    @Override
                    public void onSuccess(PlayPauseSong.ResponseValue response) {
                        openSong();
                        mView.showPlaypauseFeedback();
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }
    @Override
    public void repeatSong() {
        mUseCaseHandler.execute(mRepeatSong, new RepeatSong.RequestValues(mPlaylist.getCurrentSong()),
               new UseCase.UseCaseCallback<RepeatSong.ResponseValue>() {
                    @Override
                    public void onSuccess(RepeatSong.ResponseValue response) {
                        openSong();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public void nextSong() {
        mUseCaseHandler.execute(mNextSong, new NextSong.RequestValues(mPlaylist),
                new UseCase.UseCaseCallback<NextSong.ResponseValue>() {
                    @Override
                    public void onSuccess(NextSong.ResponseValue response) {
                        playPauseSong();
                        mView.showNextSongFeedback();
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    private void showTask(@NonNull Song song) {
        String title = song.getTitle();
        String description = song.getDescription();

        if (Strings.isNullOrEmpty(title)) {
            mView.hideTitle();
        } else {
            mView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mView.hideDescription();
        } else {
            mView.showDescription(description);
        }
    }

    public void volumeUp() {
        mUseCaseHandler.execute(mVolumeUp, new VolumeUp.RequestValues(),
                new UseCase.UseCaseCallback<VolumeUp.ResponseValue>() {
                    @Override
                    public void onSuccess(VolumeUp.ResponseValue response) {

                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    public void volumeDown() {
        mUseCaseHandler.execute(mVolumeDown, new VolumeDown.RequestValues(),
                new UseCase.UseCaseCallback<VolumeDown.ResponseValue>() {
                    @Override
                    public void onSuccess(VolumeDown.ResponseValue response) {
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    @Override
    public void updateProgress(AudioPlayerContract audioPlayer) {
        int progress = audioPlayer.percentageProgress();
        Song currentSong = mPlaylist.getCurrentSong();
        if (audioPlayer.getStatus(currentSong) == SongStatus.PLAYING) {
            mView.showSongProgress(progress);
        }
    }
}
