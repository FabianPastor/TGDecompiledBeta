package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class CompositeMediaSource<T> implements MediaSource {
    private final HashMap<T, MediaSource> childSources = new HashMap();
    private ExoPlayer player;

    protected abstract void onChildSourceInfoRefreshed(T t, MediaSource mediaSource, Timeline timeline, Object obj);

    protected CompositeMediaSource() {
    }

    public void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        this.player = exoPlayer;
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (MediaSource maybeThrowSourceInfoRefreshError : this.childSources.values()) {
            maybeThrowSourceInfoRefreshError.maybeThrowSourceInfoRefreshError();
        }
    }

    public void releaseSource() {
        for (MediaSource releaseSource : this.childSources.values()) {
            releaseSource.releaseSource();
        }
        this.childSources.clear();
        this.player = null;
    }

    protected void prepareChildSource(final T t, final MediaSource mediaSource) {
        Assertions.checkArgument(this.childSources.containsKey(t) ^ 1);
        this.childSources.put(t, mediaSource);
        mediaSource.prepareSource(this.player, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj) {
                CompositeMediaSource.this.onChildSourceInfoRefreshed(t, mediaSource, timeline, obj);
            }
        });
    }

    protected void releaseChildSource(T t) {
        ((MediaSource) this.childSources.remove(t)).releaseSource();
    }
}
