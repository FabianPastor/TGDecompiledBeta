package org.webrtc;

public interface VideoDecoder {

    public interface Callback {
        void onDecodedFrame(VideoFrame videoFrame, Integer num, Integer num2);
    }

    long createNativeVideoDecoder();

    VideoCodecStatus decode(EncodedImage encodedImage, DecodeInfo decodeInfo);

    String getImplementationName();

    boolean getPrefersLateDecoding();

    VideoCodecStatus initDecode(Settings settings, Callback callback);

    VideoCodecStatus release();

    public static class Settings {
        public final int height;
        public final int numberOfCores;
        public final int width;

        public Settings(int numberOfCores2, int width2, int height2) {
            this.numberOfCores = numberOfCores2;
            this.width = width2;
            this.height = height2;
        }
    }

    public static class DecodeInfo {
        public final boolean isMissingFrames;
        public final long renderTimeMs;

        public DecodeInfo(boolean isMissingFrames2, long renderTimeMs2) {
            this.isMissingFrames = isMissingFrames2;
            this.renderTimeMs = renderTimeMs2;
        }
    }

    /* renamed from: org.webrtc.VideoDecoder$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static long $default$createNativeVideoDecoder(VideoDecoder _this) {
            return 0;
        }
    }
}
