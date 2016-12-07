package org.telegram.messenger.exoplayer2.trackselection;

import android.os.Handler;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class TrackSelector<T> {
    private TrackSelections<T> activeSelections;
    private final Handler eventHandler;
    private InvalidationListener listener;
    private final CopyOnWriteArraySet<EventListener<? super T>> listeners = new CopyOnWriteArraySet();

    public interface EventListener<T> {
        void onTrackSelectionsChanged(TrackSelections<? extends T> trackSelections);
    }

    public interface InvalidationListener {
        void onTrackSelectionsInvalidated();
    }

    public abstract TrackSelections<T> selectTracks(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray trackGroupArray) throws ExoPlaybackException;

    public TrackSelector(Handler eventHandler) {
        this.eventHandler = (Handler) Assertions.checkNotNull(eventHandler);
    }

    public final void addListener(EventListener<? super T> listener) {
        this.listeners.add(listener);
    }

    public final void removeListener(EventListener<? super T> listener) {
        this.listeners.remove(listener);
    }

    public final TrackSelections<T> getCurrentSelections() {
        return this.activeSelections;
    }

    public final void init(InvalidationListener listener) {
        this.listener = listener;
    }

    public final void onSelectionActivated(TrackSelections<T> activeSelections) {
        this.activeSelections = activeSelections;
        notifyTrackSelectionsChanged(activeSelections);
    }

    protected final void invalidate() {
        if (this.listener != null) {
            this.listener.onTrackSelectionsInvalidated();
        }
    }

    private void notifyTrackSelectionsChanged(final TrackSelections<T> activeSelections) {
        if (this.eventHandler != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    Iterator it = TrackSelector.this.listeners.iterator();
                    while (it.hasNext()) {
                        ((EventListener) it.next()).onTrackSelectionsChanged(activeSelections);
                    }
                }
            });
        }
    }
}
