package org.webrtc;

public class VideoDecoderFallback extends WrappedNativeVideoDecoder {
    private final VideoDecoder fallback;
    private final VideoDecoder primary;

    private static native long nativeCreateDecoder(VideoDecoder videoDecoder, VideoDecoder videoDecoder2);

    public VideoDecoderFallback(VideoDecoder fallback2, VideoDecoder primary2) {
        this.fallback = fallback2;
        this.primary = primary2;
    }

    public long createNativeVideoDecoder() {
        return nativeCreateDecoder(this.fallback, this.primary);
    }
}
