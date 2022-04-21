package org.webrtc;

public class MediaStreamTrack {
    public static final String AUDIO_TRACK_KIND = "audio";
    public static final String VIDEO_TRACK_KIND = "video";
    private long nativeTrack;

    private static native boolean nativeGetEnabled(long j);

    private static native String nativeGetId(long j);

    private static native String nativeGetKind(long j);

    private static native State nativeGetState(long j);

    private static native boolean nativeSetEnabled(long j, boolean z);

    public enum State {
        LIVE,
        ENDED;

        static State fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    public enum MediaType {
        MEDIA_TYPE_AUDIO(0),
        MEDIA_TYPE_VIDEO(1);
        
        private final int nativeIndex;

        private MediaType(int nativeIndex2) {
            this.nativeIndex = nativeIndex2;
        }

        /* access modifiers changed from: package-private */
        public int getNative() {
            return this.nativeIndex;
        }

        static MediaType fromNativeIndex(int nativeIndex2) {
            for (MediaType type : values()) {
                if (type.getNative() == nativeIndex2) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown native media type: " + nativeIndex2);
        }
    }

    static MediaStreamTrack createMediaStreamTrack(long nativeTrack2) {
        if (nativeTrack2 == 0) {
            return null;
        }
        String trackKind = nativeGetKind(nativeTrack2);
        if (trackKind.equals("audio")) {
            return new AudioTrack(nativeTrack2);
        }
        if (trackKind.equals("video")) {
            return new VideoTrack(nativeTrack2);
        }
        return null;
    }

    public MediaStreamTrack(long nativeTrack2) {
        if (nativeTrack2 != 0) {
            this.nativeTrack = nativeTrack2;
            return;
        }
        throw new IllegalArgumentException("nativeTrack may not be null");
    }

    public String id() {
        checkMediaStreamTrackExists();
        return nativeGetId(this.nativeTrack);
    }

    public String kind() {
        checkMediaStreamTrackExists();
        return nativeGetKind(this.nativeTrack);
    }

    public boolean enabled() {
        checkMediaStreamTrackExists();
        return nativeGetEnabled(this.nativeTrack);
    }

    public boolean setEnabled(boolean enable) {
        checkMediaStreamTrackExists();
        return nativeSetEnabled(this.nativeTrack, enable);
    }

    public State state() {
        checkMediaStreamTrackExists();
        return nativeGetState(this.nativeTrack);
    }

    public void dispose() {
        checkMediaStreamTrackExists();
        JniCommon.nativeReleaseRef(this.nativeTrack);
        this.nativeTrack = 0;
    }

    /* access modifiers changed from: package-private */
    public long getNativeMediaStreamTrack() {
        checkMediaStreamTrackExists();
        return this.nativeTrack;
    }

    private void checkMediaStreamTrackExists() {
        if (this.nativeTrack == 0) {
            throw new IllegalStateException("MediaStreamTrack has been disposed.");
        }
    }
}
