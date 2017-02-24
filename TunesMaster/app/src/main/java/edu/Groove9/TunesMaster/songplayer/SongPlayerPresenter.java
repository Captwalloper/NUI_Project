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
import edu.Groove9.TunesMaster.addedittask.domain.usecase.DeleteTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.LastSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.NextSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.PlayPauseSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.RepeatSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.ShuffleSong;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link SongPlayerFragment}), retrieves the data and updates
 * the UI as required.
 */
public class SongPlayerPresenter implements SongPlayerContract.Presenter {

    private final SongPlayerContract.View mTaskDetailView;
    private final UseCaseHandler mUseCaseHandler;
    private final GetTask mGetTask;
    private final DeleteTask mDeleteTask;
    private final PlayPauseSong mPlayPauseSong;
    private final RepeatSong mRepeatSong;
    private final NextSong mNextSong;
    private final LastSong mLastSong;
    private final ShuffleSong mShuffleSong;

    @Nullable
    private Playlist mPlaylist;

    public SongPlayerPresenter(@NonNull UseCaseHandler useCaseHandler,
                               @Nullable Playlist playlist,
                               @NonNull SongPlayerContract.View taskDetailView,
                               @NonNull GetTask getTask,
                               @NonNull DeleteTask deleteTask,
                               @NonNull PlayPauseSong playPauseSong,
                               @NonNull NextSong nextSong,
                               @NonNull LastSong lastSong,
                               @NonNull ShuffleSong shuffleSong,
                               @NonNull RepeatSong repeatSong) {
        mPlaylist = playlist;
        mUseCaseHandler = checkNotNull(useCaseHandler, "useCaseHandler cannot be null!");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mGetTask = checkNotNull(getTask, "getSong cannot be null!");
        mDeleteTask = checkNotNull(deleteTask, "deleteSong cannot be null!");
        mPlayPauseSong = playPauseSong;
        mNextSong = nextSong;
        mLastSong = lastSong;
        mShuffleSong = shuffleSong;
        mTaskDetailView.setPresenter(this);
        mRepeatSong=repeatSong;
    }



    @Override
    public void start() {
        openSong();
    }

    private void openSong() {
        if (Strings.isNullOrEmpty(mPlaylist.getCurrentSong().getId())) {
            mTaskDetailView.showMissingSong();
            return;
        }

        mTaskDetailView.setLoadingIndicator(true);

        mUseCaseHandler.execute(mGetTask, new GetTask.RequestValues(mPlaylist.getCurrentSong().getId()),
                new UseCase.UseCaseCallback<GetTask.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTask.ResponseValue response) {
                        Song song = response.getTask();

                        // The view may not be able to handle UI updates anymore
                        mTaskDetailView.setLoadingIndicator(false);
                        showTask(song);
                    }

                    @Override
                    public void onError() {
                        // The view may not be able to handle UI updates anymore
                        mTaskDetailView.showMissingSong();
                    }
                });
    }

    @Override
    public void editSong() {
        if (Strings.isNullOrEmpty(mPlaylist.getCurrentSong().getId())) {
            mTaskDetailView.showMissingSong();
            return;
        }
        mTaskDetailView.showEditSong(mPlaylist.getCurrentSong().getId());
    }

    @Override
    public void deleteSong() {
        mUseCaseHandler.execute(mDeleteTask, new DeleteTask.RequestValues(mPlaylist.getCurrentSong().getId()),
                new UseCase.UseCaseCallback<DeleteTask.ResponseValue>() {
                    @Override
                    public void onSuccess(DeleteTask.ResponseValue response) {
                        mTaskDetailView.showSongDeleted();
                    }

                    @Override
                    public void onError() {
                        // Show error, log, etc.
                    }
                });
    }

    @Override
    public void shuffleSong() {
        mUseCaseHandler.execute(mShuffleSong, new ShuffleSong.RequestValues(mPlaylist),
                new UseCase.UseCaseCallback<ShuffleSong.ResponseValue>() {
                    @Override
                    public void onSuccess(ShuffleSong.ResponseValue response) {
                        playPauseSong();
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
                    public void onSuccess(RepeatSong.ResponseValue response) {openSong();}

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
            mTaskDetailView.hideTitle();
        } else {
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        } else {
            mTaskDetailView.showDescription(description);
        }
    }
}
