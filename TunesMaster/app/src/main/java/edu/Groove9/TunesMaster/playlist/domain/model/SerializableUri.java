package edu.Groove9.TunesMaster.playlist.domain.model;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ConnorM on 2/22/2017.
 */
@SuppressWarnings("serial")
public class SerializableUri implements Serializable {
    @NonNull
    private transient Uri uri;

    private String path;

    public SerializableUri(Uri uri) {
        checkNotNull(uri);
        setUri(uri);
    }

    @NonNull
    public Uri getUri() {
        if (uri == null || uri.getPath() == null || uri.getPath().isEmpty()) {
            uri = Uri.parse(path);
        }
        return uri;
    }

    public void setUri(@NonNull Uri uri) {
        checkNotNull(uri);
        this.uri = uri;
        setPath(uri.getPath());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
