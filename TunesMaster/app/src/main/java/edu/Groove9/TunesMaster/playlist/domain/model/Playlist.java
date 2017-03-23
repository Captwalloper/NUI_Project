package edu.Groove9.TunesMaster.playlist.domain.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by ConnorM on 2/22/2017.
 */

@SuppressWarnings("serial")
public class Playlist implements Serializable {

    @NonNull
    private List<Song> songs;

    @NonNull
    private Song currentSong = null;

    public Playlist(List<Song> songs) {
        ensureIsValid(songs);
        this.songs = songs;
        if (!songs.isEmpty()) {
            currentSong = songs.get(0);
        }
    }

    public Playlist(Song... songs) {
        this(Arrays.asList(songs));
    }

    public Playlist(List<Song> songs, Song currentSong) {
        this(songs);
        this.currentSong = currentSong;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public Song getNextSong() {
        if (songs.isEmpty() || currentSong == null) {
            return null;
        }

        int currentIndex = songs.indexOf(currentSong);
        int lastIndex = songs.size() - 1;
        if (currentIndex == lastIndex) {
            currentSong = songs.get(0);
        } else {
            currentSong = songs.get(++currentIndex);
        }

        return currentSong;
    }

    public Song getLastSong() {
        if (songs.isEmpty() || currentSong == null) {
            return null;
        }

        int currentIndex = songs.indexOf(currentSong);
        int firstIndex = 0;
        if (currentIndex == firstIndex) {
            int lastIndex = songs.size() - 1;
            currentSong = songs.get(lastIndex);
        } else {
            currentSong = songs.get(--currentIndex);
        }

        return currentSong;
    }

    public void Shuffle() {
        if (songs.isEmpty()) {
            return;
        }

        // shuffle songs
        long seed = System.nanoTime();
        Collections.shuffle(songs, new Random(seed));

        // randomly choose currentSong
        int min = 0;
        int max = songs.size() - 1;
        int randomIndex = min + (int)(Math.random() * ((max - min) + 1));
        currentSong = songs.get(randomIndex);
    }

    private static void ensureIsValid(List<Song> songs) {
        for (Song song : songs) {
            if (song == null) {
                throw new RuntimeException("Null song in playlist");
            }
        }
    }

    // for serializability

    @NonNull
    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(@NonNull List<Song> songs) {
        this.songs = songs;
    }

    public void setCurrentSong(@NonNull Song currentSong) {
        this.currentSong = currentSong;
    }
}
