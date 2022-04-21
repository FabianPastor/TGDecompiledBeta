package org.webrtc;

import java.util.IdentityHashMap;

public class VideoTrack extends MediaStreamTrack {
    private final IdentityHashMap<VideoSink, Long> sinks = new IdentityHashMap<>();

    private static native void nativeAddSink(long j, long j2);

    private static native void nativeFreeSink(long j);

    private static native void nativeRemoveSink(long j, long j2);

    private static native long nativeWrapSink(VideoSink videoSink);

    public VideoTrack(long nativeTrack) {
        super(nativeTrack);
    }

    public void addSink(VideoSink sink) {
        if (sink == null) {
            throw new IllegalArgumentException("The VideoSink is not allowed to be null");
        } else if (!this.sinks.containsKey(sink)) {
            long nativeSink = nativeWrapSink(sink);
            this.sinks.put(sink, Long.valueOf(nativeSink));
            nativeAddSink(getNativeMediaStreamTrack(), nativeSink);
        }
    }

    public void removeSink(VideoSink sink) {
        Long nativeSink = this.sinks.remove(sink);
        if (nativeSink != null) {
            nativeRemoveSink(getNativeMediaStreamTrack(), nativeSink.longValue());
            nativeFreeSink(nativeSink.longValue());
        }
    }

    public void dispose() {
        for (Long longValue : this.sinks.values()) {
            long nativeSink = longValue.longValue();
            nativeRemoveSink(getNativeMediaStreamTrack(), nativeSink);
            nativeFreeSink(nativeSink);
        }
        this.sinks.clear();
        super.dispose();
    }

    /* access modifiers changed from: package-private */
    public long getNativeVideoTrack() {
        return getNativeMediaStreamTrack();
    }
}
