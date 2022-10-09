package org.webrtc;

import org.webrtc.VideoFrame;
import org.webrtc.VideoProcessor;
/* loaded from: classes3.dex */
public class NativeCapturerObserver implements CapturerObserver {
    private final NativeAndroidVideoTrackSource nativeAndroidVideoTrackSource;

    @CalledByNative
    public NativeCapturerObserver(long j) {
        this.nativeAndroidVideoTrackSource = new NativeAndroidVideoTrackSource(j);
    }

    @Override // org.webrtc.CapturerObserver
    public void onCapturerStarted(boolean z) {
        this.nativeAndroidVideoTrackSource.setState(z);
    }

    @Override // org.webrtc.CapturerObserver
    public void onCapturerStopped() {
        this.nativeAndroidVideoTrackSource.setState(false);
    }

    public NativeAndroidVideoTrackSource getNativeAndroidVideoTrackSource() {
        return this.nativeAndroidVideoTrackSource;
    }

    @Override // org.webrtc.CapturerObserver
    public void onFrameCaptured(VideoFrame videoFrame) {
        VideoProcessor.FrameAdaptationParameters adaptFrame = this.nativeAndroidVideoTrackSource.adaptFrame(videoFrame);
        if (adaptFrame == null || adaptFrame.cropWidth == 0 || adaptFrame.cropHeight == 0) {
            return;
        }
        VideoFrame.Buffer cropAndScale = videoFrame.getBuffer().cropAndScale(adaptFrame.cropX, adaptFrame.cropY, adaptFrame.cropWidth, adaptFrame.cropHeight, adaptFrame.scaleWidth, adaptFrame.scaleHeight);
        this.nativeAndroidVideoTrackSource.onFrameCaptured(new VideoFrame(cropAndScale, videoFrame.getRotation(), adaptFrame.timestampNs));
        cropAndScale.release();
    }
}
