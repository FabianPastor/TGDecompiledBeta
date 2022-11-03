package org.webrtc;
/* loaded from: classes.dex */
public class AudioSource extends MediaSource {
    public AudioSource(long j) {
        super(j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNativeAudioSource() {
        return getNativeMediaSource();
    }
}
