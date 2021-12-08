package org.webrtc;

public class AudioSource extends MediaSource {
    public AudioSource(long nativeSource) {
        super(nativeSource);
    }

    /* access modifiers changed from: package-private */
    public long getNativeAudioSource() {
        return getNativeMediaSource();
    }
}
