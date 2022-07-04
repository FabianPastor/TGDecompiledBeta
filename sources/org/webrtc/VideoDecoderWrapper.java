package org.webrtc;

import org.webrtc.VideoDecoder;

class VideoDecoderWrapper {
    /* access modifiers changed from: private */
    public static native void nativeOnDecodedFrame(long j, VideoFrame videoFrame, Integer num, Integer num2);

    VideoDecoderWrapper() {
    }

    static VideoDecoder.Callback createDecoderCallback(long nativeDecoder) {
        return new VideoDecoderWrapper$$ExternalSyntheticLambda0(nativeDecoder);
    }
}
