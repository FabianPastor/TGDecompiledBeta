package org.webrtc;

import org.webrtc.VideoEncoder;

class VideoEncoderWrapper {
    /* access modifiers changed from: private */
    public static native void nativeOnEncodedFrame(long j, EncodedImage encodedImage);

    VideoEncoderWrapper() {
    }

    @CalledByNative
    static boolean getScalingSettingsOn(VideoEncoder.ScalingSettings scalingSettings) {
        return scalingSettings.on;
    }

    @CalledByNative
    static Integer getScalingSettingsLow(VideoEncoder.ScalingSettings scalingSettings) {
        return scalingSettings.low;
    }

    @CalledByNative
    static Integer getScalingSettingsHigh(VideoEncoder.ScalingSettings scalingSettings) {
        return scalingSettings.high;
    }

    @CalledByNative
    static VideoEncoder.Callback createEncoderCallback(long j) {
        return new VideoEncoder.Callback(j) {
            public final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final void onEncodedFrame(EncodedImage encodedImage, VideoEncoder.CodecSpecificInfo codecSpecificInfo) {
                VideoEncoderWrapper.nativeOnEncodedFrame(this.f$0, encodedImage);
            }
        };
    }
}
