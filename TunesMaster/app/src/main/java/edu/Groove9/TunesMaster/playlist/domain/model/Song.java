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

package edu.Groove9.TunesMaster.playlist.domain.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Immutable model class for a Song.
 */
public final class Song {

    @NonNull
    private final String mId;

    @NonNull
    private final Uri mSource;

    @Nullable
    private final String mTitle;

    @Nullable
    private final String mDescription;

    private final boolean mCompleted;

    /**
     * Use this constructor to create a new active Song.
     *
     * @param title       title of the task
     * @param description description of the task
     */
    public Song(@Nullable String title, @Nullable String description, @NonNull Uri source) {
        this(title, description, UUID.randomUUID().toString(), false, source);
    }

    /**
     * Use this constructor to create an active Song if the Song already has an id (copy of another
     * Song).
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     */
    public Song(@Nullable String title, @Nullable String description, @NonNull String id, @NonNull Uri source) {
        this(title, description, id, false, source);
    }

    /**
     * Use this constructor to create a new completed Song.
     *
     * @param title       title of the task
     * @param description description of the task
     * @param completed   true if the task is completed, false if it's active
     */
    public Song(@Nullable String title, @Nullable String description, boolean completed, @NonNull Uri source) {
        this(title, description, UUID.randomUUID().toString(), completed, source);
    }

    /**
     * Use this constructor to specify a completed Song if the Song already has an id (copy of
     * another Song).
     *
     * @param title       title of the task
     * @param description description of the task
     * @param id          id of the task
     * @param completed   true if the task is completed, false if it's active
     */
    public Song(@Nullable String title, @Nullable String description,
                @NonNull String id, boolean completed, @NonNull Uri source) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mCompleted = completed;
        mSource = source;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Uri getSource() { return mSource; }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getTitleForList() {
        if (!Strings.isNullOrEmpty(mTitle)) {
            return mTitle;
        } else {
            return mDescription;
        }
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public boolean isActive() {
        return !mCompleted;
    }

    public boolean isEmpty() {
        return Strings.isNullOrEmpty(mTitle) &&
               Strings.isNullOrEmpty(mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equal(mId, song.mId) &&
               Objects.equal(mTitle, song.mTitle) &&
               Objects.equal(mDescription, song.mDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mId, mTitle, mDescription);
    }

    @Override
    public String toString() {
        return "Song with title " + mTitle;
    }
}
