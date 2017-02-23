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

import java.io.Serializable;
import java.util.UUID;

/**
 * Immutable model class for a Song.
 */
@SuppressWarnings("serial")
public final class Song implements Serializable {

    @NonNull
    private String mId;

    @NonNull
    private SerializableUri mSource;

    @Nullable
    private String mTitle;

    @Nullable
    private String mDescription;

    /**
     * Use this constructor to create a new active Song.
     *
     * @param title       title of the task
     * @param description description of the task
     */
    public Song(@Nullable String title, @Nullable String description, @NonNull Uri source) {
        this(title, description, UUID.randomUUID().toString(), source);
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
        mId = id;
        mTitle = title;
        mDescription = description;
        mSource = new SerializableUri(source);
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Uri getSource() { return mSource.getUri(); }

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

    // Setters for serializablility

    public void setmId(@NonNull String mId) {
        this.mId = mId;
    }

    public void setmTitle(@Nullable String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDescription(@Nullable String mDescription) {
        this.mDescription = mDescription;
    }

    @NonNull
    public SerializableUri getmSource() {
        return mSource;
    }

    public void setmSource(@NonNull SerializableUri mSource) {
        this.mSource = mSource;
    }
}
