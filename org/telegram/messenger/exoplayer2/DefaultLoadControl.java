package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultLoadControl implements LoadControl {
    private static final int ABOVE_HIGH_WATERMARK = 0;
    private static final int BELOW_LOW_WATERMARK = 2;
    private static final int BETWEEN_WATERMARKS = 1;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
    public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
    public static final int DEFAULT_MAX_BUFFER_MS = 30000;
    public static final int DEFAULT_MIN_BUFFER_MS = 15000;
    private final DefaultAllocator allocator;
    private final long bufferForPlaybackAfterRebufferUs;
    private final long bufferForPlaybackUs;
    private boolean isBuffering;
    private final long maxBufferUs;
    private final long minBufferUs;
    private int targetBufferSize;

    public DefaultLoadControl() {
        this(new DefaultAllocator(true, 65536));
    }

    public DefaultLoadControl(DefaultAllocator allocator) {
        this(allocator, DEFAULT_MIN_BUFFER_MS, DEFAULT_MAX_BUFFER_MS, 2500, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public DefaultLoadControl(DefaultAllocator allocator, int minBufferMs, int maxBufferMs, long bufferForPlaybackMs, long bufferForPlaybackAfterRebufferMs) {
        this.allocator = allocator;
        this.minBufferUs = ((long) minBufferMs) * 1000;
        this.maxBufferUs = ((long) maxBufferMs) * 1000;
        this.bufferForPlaybackUs = bufferForPlaybackMs * 1000;
        this.bufferForPlaybackAfterRebufferUs = bufferForPlaybackAfterRebufferMs * 1000;
    }

    public void onPrepared() {
        reset(false);
    }

    public void onTracksSelected(Renderer[] renderers, TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        this.targetBufferSize = 0;
        for (int i = 0; i < renderers.length; i++) {
            if (trackSelections.get(i) != null) {
                this.targetBufferSize += Util.getDefaultBufferSize(renderers[i].getTrackType());
            }
        }
        this.allocator.setTargetBufferSize(this.targetBufferSize);
    }

    public void onStopped() {
        reset(true);
    }

    public void onReleased() {
        reset(true);
    }

    public Allocator getAllocator() {
        return this.allocator;
    }

    public boolean shouldStartPlayback(long bufferedDurationUs, boolean rebuffering) {
        long minBufferDurationUs = rebuffering ? this.bufferForPlaybackAfterRebufferUs : this.bufferForPlaybackUs;
        return minBufferDurationUs <= 0 || bufferedDurationUs >= minBufferDurationUs;
    }

    public boolean shouldContinueLoading(long bufferedDurationUs) {
        boolean z = false;
        int bufferTimeState = getBufferTimeState(bufferedDurationUs);
        boolean targetBufferSizeReached;
        if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize) {
            targetBufferSizeReached = true;
        } else {
            targetBufferSizeReached = false;
        }
        if (bufferTimeState == 2 || (bufferTimeState == 1 && this.isBuffering && !targetBufferSizeReached)) {
            z = true;
        }
        this.isBuffering = z;
        return this.isBuffering;
    }

    private int getBufferTimeState(long bufferedDurationUs) {
        if (bufferedDurationUs > this.maxBufferUs) {
            return 0;
        }
        return bufferedDurationUs < this.minBufferUs ? 2 : 1;
    }

    private void reset(boolean resetAllocator) {
        this.targetBufferSize = 0;
        this.isBuffering = false;
        if (resetAllocator) {
            this.allocator.reset();
        }
    }
}
