package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelections;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public interface LoadControl {
    Allocator getAllocator();

    void onPrepared();

    void onReleased();

    void onStopped();

    void onTracksSelected(Renderer[] rendererArr, TrackGroupArray trackGroupArray, TrackSelections<?> trackSelections);

    boolean shouldContinueLoading(long j);

    boolean shouldStartPlayback(long j, boolean z);
}