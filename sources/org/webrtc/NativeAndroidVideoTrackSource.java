package org.webrtc;

import org.webrtc.VideoFrame;
import org.webrtc.VideoProcessor;
import org.webrtc.VideoSource;

public class NativeAndroidVideoTrackSource {
    private final long nativeAndroidVideoTrackSource;

    private static native VideoProcessor.FrameAdaptationParameters nativeAdaptFrame(long j, int i, int i2, int i3, long j2);

    private static native void nativeAdaptOutputFormat(long j, int i, int i2, Integer num, int i3, int i4, Integer num2, Integer num3);

    private static native void nativeOnFrameCaptured(long j, int i, long j2, VideoFrame.Buffer buffer);

    private static native void nativeSetIsScreencast(long j, boolean z);

    private static native void nativeSetState(long j, boolean z);

    public NativeAndroidVideoTrackSource(long nativeAndroidVideoTrackSource2) {
        this.nativeAndroidVideoTrackSource = nativeAndroidVideoTrackSource2;
    }

    public void setState(boolean isLive) {
        nativeSetState(this.nativeAndroidVideoTrackSource, isLive);
    }

    public VideoProcessor.FrameAdaptationParameters adaptFrame(VideoFrame frame) {
        return nativeAdaptFrame(this.nativeAndroidVideoTrackSource, frame.getBuffer().getWidth(), frame.getBuffer().getHeight(), frame.getRotation(), frame.getTimestampNs());
    }

    public void onFrameCaptured(VideoFrame frame) {
        nativeOnFrameCaptured(this.nativeAndroidVideoTrackSource, frame.getRotation(), frame.getTimestampNs(), frame.getBuffer());
    }

    public void adaptOutputFormat(VideoSource.AspectRatio targetLandscapeAspectRatio, Integer maxLandscapePixelCount, VideoSource.AspectRatio targetPortraitAspectRatio, Integer maxPortraitPixelCount, Integer maxFps) {
        nativeAdaptOutputFormat(this.nativeAndroidVideoTrackSource, targetLandscapeAspectRatio.width, targetLandscapeAspectRatio.height, maxLandscapePixelCount, targetPortraitAspectRatio.width, targetPortraitAspectRatio.height, maxPortraitPixelCount, maxFps);
    }

    public void setIsScreencast(boolean isScreencast) {
        nativeSetIsScreencast(this.nativeAndroidVideoTrackSource, isScreencast);
    }

    static VideoProcessor.FrameAdaptationParameters createFrameAdaptationParameters(int cropX, int cropY, int cropWidth, int cropHeight, int scaleWidth, int scaleHeight, long timestampNs, boolean drop) {
        return new VideoProcessor.FrameAdaptationParameters(cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight, timestampNs, drop);
    }
}
