package org.webrtc;

public interface VideoEncoderFactory {

    public interface VideoEncoderSelector {
        VideoCodecInfo onAvailableBitrate(int i);

        void onCurrentEncoder(VideoCodecInfo videoCodecInfo);

        VideoCodecInfo onEncoderBroken();
    }

    VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo);

    VideoEncoderSelector getEncoderSelector();

    VideoCodecInfo[] getImplementations();

    VideoCodecInfo[] getSupportedCodecs();

    /* renamed from: org.webrtc.VideoEncoderFactory$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static VideoCodecInfo[] $default$getImplementations(VideoEncoderFactory _this) {
            return _this.getSupportedCodecs();
        }

        public static VideoEncoderSelector $default$getEncoderSelector(VideoEncoderFactory _this) {
            return null;
        }
    }
}
