package org.webrtc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaStream {
    private static final String TAG = "MediaStream";
    public final List<AudioTrack> audioTracks = new ArrayList();
    private long nativeStream;
    public final List<VideoTrack> preservedVideoTracks = new ArrayList();
    public final List<VideoTrack> videoTracks = new ArrayList();

    private static native boolean nativeAddAudioTrackToNativeStream(long j, long j2);

    private static native boolean nativeAddVideoTrackToNativeStream(long j, long j2);

    private static native String nativeGetId(long j);

    private static native boolean nativeRemoveAudioTrack(long j, long j2);

    private static native boolean nativeRemoveVideoTrack(long j, long j2);

    public MediaStream(long nativeStream2) {
        this.nativeStream = nativeStream2;
    }

    public boolean addTrack(AudioTrack track) {
        checkMediaStreamExists();
        if (!nativeAddAudioTrackToNativeStream(this.nativeStream, track.getNativeAudioTrack())) {
            return false;
        }
        this.audioTracks.add(track);
        return true;
    }

    public boolean addTrack(VideoTrack track) {
        checkMediaStreamExists();
        if (!nativeAddVideoTrackToNativeStream(this.nativeStream, track.getNativeVideoTrack())) {
            return false;
        }
        this.videoTracks.add(track);
        return true;
    }

    public boolean addPreservedTrack(VideoTrack track) {
        checkMediaStreamExists();
        if (!nativeAddVideoTrackToNativeStream(this.nativeStream, track.getNativeVideoTrack())) {
            return false;
        }
        this.preservedVideoTracks.add(track);
        return true;
    }

    public boolean removeTrack(AudioTrack track) {
        checkMediaStreamExists();
        this.audioTracks.remove(track);
        return nativeRemoveAudioTrack(this.nativeStream, track.getNativeAudioTrack());
    }

    public boolean removeTrack(VideoTrack track) {
        checkMediaStreamExists();
        this.videoTracks.remove(track);
        this.preservedVideoTracks.remove(track);
        return nativeRemoveVideoTrack(this.nativeStream, track.getNativeVideoTrack());
    }

    public void dispose() {
        checkMediaStreamExists();
        while (!this.audioTracks.isEmpty()) {
            AudioTrack track = this.audioTracks.get(0);
            removeTrack(track);
            track.dispose();
        }
        while (!this.videoTracks.isEmpty()) {
            VideoTrack track2 = this.videoTracks.get(0);
            removeTrack(track2);
            track2.dispose();
        }
        while (!this.preservedVideoTracks.isEmpty()) {
            removeTrack(this.preservedVideoTracks.get(0));
        }
        JniCommon.nativeReleaseRef(this.nativeStream);
        this.nativeStream = 0;
    }

    public String getId() {
        checkMediaStreamExists();
        return nativeGetId(this.nativeStream);
    }

    public String toString() {
        return "[" + getId() + ":A=" + this.audioTracks.size() + ":V=" + this.videoTracks.size() + "]";
    }

    /* access modifiers changed from: package-private */
    public void addNativeAudioTrack(long nativeTrack) {
        this.audioTracks.add(new AudioTrack(nativeTrack));
    }

    /* access modifiers changed from: package-private */
    public void addNativeVideoTrack(long nativeTrack) {
        this.videoTracks.add(new VideoTrack(nativeTrack));
    }

    /* access modifiers changed from: package-private */
    public void removeAudioTrack(long nativeTrack) {
        removeMediaStreamTrack(this.audioTracks, nativeTrack);
    }

    /* access modifiers changed from: package-private */
    public void removeVideoTrack(long nativeTrack) {
        removeMediaStreamTrack(this.videoTracks, nativeTrack);
    }

    /* access modifiers changed from: package-private */
    public long getNativeMediaStream() {
        checkMediaStreamExists();
        return this.nativeStream;
    }

    private void checkMediaStreamExists() {
        if (this.nativeStream == 0) {
            throw new IllegalStateException("MediaStream has been disposed.");
        }
    }

    private static void removeMediaStreamTrack(List<? extends MediaStreamTrack> tracks, long nativeTrack) {
        Iterator<? extends MediaStreamTrack> it = tracks.iterator();
        while (it.hasNext()) {
            MediaStreamTrack track = (MediaStreamTrack) it.next();
            if (track.getNativeMediaStreamTrack() == nativeTrack) {
                track.dispose();
                it.remove();
                return;
            }
        }
        Logging.e("MediaStream", "Couldn't not find track");
    }
}
