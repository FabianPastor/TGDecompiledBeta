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

    public void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        this.player = player;
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (MediaSource childSource : this.childSources.values()) {
            childSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public void releaseSource() {
        for (MediaSource childSource : this.childSources.values()) {
            childSource.releaseSource();
        }
        this.childSources.clear();
        this.player = null;
    }

    protected void prepareChildSource(final T id, final MediaSource mediaSource) {
        Assertions.checkArgument(this.childSources.containsKey(id) ^ 1);
        this.childSources.put(id, mediaSource);
        mediaSource.prepareSource(this.player, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource source, Timeline timeline, Object manifest) {
                CompositeMediaSource.this.onChildSourceInfoRefreshed(id, mediaSource, timeline, manifest);
            }
        });
    }

    protected void releaseChildSource(T id) {
        ((MediaSource) this.childSources.remove(id)).releaseSource();
    }
}
