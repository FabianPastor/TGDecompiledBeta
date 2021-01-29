package org.webrtc;

public class AudioSource extends MediaSource {
    public AudioSource(long j) {
        super(j);
    }

    /* access modifiers changed from: package-private */
    public long getNativeAudioSource() {
        return getNativeMediaSource();
    }
}
