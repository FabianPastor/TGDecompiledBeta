package org.webrtc;

public interface VideoDecoderFactory {
    @Deprecated
    VideoDecoder createDecoder(String str);

    VideoDecoder createDecoder(VideoCodecInfo videoCodecInfo);

    VideoCodecInfo[] getSupportedCodecs();

    /* renamed from: org.webrtc.VideoDecoderFactory$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        @Deprecated
        public static VideoDecoder $default$createDecoder(VideoDecoderFactory _this, String codecType) {
            throw new UnsupportedOperationException("Deprecated and not implemented.");
        }

        public static VideoDecoder $default$createDecoder(VideoDecoderFactory _this, VideoCodecInfo info) {
            return _this.createDecoder(info.getName());
        }

        public static VideoCodecInfo[] $default$getSupportedCodecs(VideoDecoderFactory _this) {
            return new VideoCodecInfo[0];
        }
    }
}
