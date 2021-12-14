package org.webrtc;

public interface VideoEncoderFactory {

    public interface VideoEncoderSelector {
        @CalledByNative("VideoEncoderSelector")
        VideoCodecInfo onAvailableBitrate(int i);

        @CalledByNative("VideoEncoderSelector")
        void onCurrentEncoder(VideoCodecInfo videoCodecInfo);

        @CalledByNative("VideoEncoderSelector")
        VideoCodecInfo onEncoderBroken();
    }

    @CalledByNative
    VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo);

    @CalledByNative
    VideoEncoderSelector getEncoderSelector();

    @CalledByNative
    VideoCodecInfo[] getImplementations();

    @CalledByNative
    VideoCodecInfo[] getSupportedCodecs();

    /* renamed from: org.webrtc.VideoEncoderFactory$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        @CalledByNative
        public static VideoEncoderSelector $default$getEncoderSelector(VideoEncoderFactory videoEncoderFactory) {
            return null;
        }

        @CalledByNative
        public static VideoCodecInfo[] $default$getImplementations(VideoEncoderFactory _this) {
            return _this.getSupportedCodecs();
        }
    }
}
