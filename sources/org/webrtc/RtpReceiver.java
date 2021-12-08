package org.webrtc;

import org.webrtc.MediaStreamTrack;

public class RtpReceiver {
    private MediaStreamTrack cachedTrack;
    private long nativeObserver;
    private long nativeRtpReceiver;

    public interface Observer {
        void onFirstPacketReceived(MediaStreamTrack.MediaType mediaType);
    }

    private static native String nativeGetId(long j);

    private static native RtpParameters nativeGetParameters(long j);

    private static native long nativeGetTrack(long j);

    private static native void nativeSetFrameDecryptor(long j, long j2);

    private static native long nativeSetObserver(long j, Observer observer);

    private static native void nativeUnsetObserver(long j, long j2);

    public RtpReceiver(long nativeRtpReceiver2) {
        this.nativeRtpReceiver = nativeRtpReceiver2;
        this.cachedTrack = MediaStreamTrack.createMediaStreamTrack(nativeGetTrack(nativeRtpReceiver2));
    }

    public MediaStreamTrack track() {
        return this.cachedTrack;
    }

    public RtpParameters getParameters() {
        checkRtpReceiverExists();
        return nativeGetParameters(this.nativeRtpReceiver);
    }

    public String id() {
        checkRtpReceiverExists();
        return nativeGetId(this.nativeRtpReceiver);
    }

    public void dispose() {
        checkRtpReceiverExists();
        this.cachedTrack.dispose();
        long j = this.nativeObserver;
        if (j != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, j);
            this.nativeObserver = 0;
        }
        JniCommon.nativeReleaseRef(this.nativeRtpReceiver);
        this.nativeRtpReceiver = 0;
    }

    public void SetObserver(Observer observer) {
        checkRtpReceiverExists();
        long j = this.nativeObserver;
        if (j != 0) {
            nativeUnsetObserver(this.nativeRtpReceiver, j);
        }
        this.nativeObserver = nativeSetObserver(this.nativeRtpReceiver, observer);
    }

    public void setFrameDecryptor(FrameDecryptor frameDecryptor) {
        checkRtpReceiverExists();
        nativeSetFrameDecryptor(this.nativeRtpReceiver, frameDecryptor.getNativeFrameDecryptor());
    }

    private void checkRtpReceiverExists() {
        if (this.nativeRtpReceiver == 0) {
            throw new IllegalStateException("RtpReceiver has been disposed.");
        }
    }
}
