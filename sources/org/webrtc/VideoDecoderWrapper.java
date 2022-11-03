package org.webrtc;

import org.webrtc.VideoDecoder;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class VideoDecoderWrapper {
    /* JADX INFO: Access modifiers changed from: private */
    public static native void nativeOnDecodedFrame(long j, VideoFrame videoFrame, Integer num, Integer num2);

    VideoDecoderWrapper() {
    }

    @CalledByNative
    static VideoDecoder.Callback createDecoderCallback(final long j) {
        return new VideoDecoder.Callback() { // from class: org.webrtc.VideoDecoderWrapper$$ExternalSyntheticLambda0
            @Override // org.webrtc.VideoDecoder.Callback
            public final void onDecodedFrame(VideoFrame videoFrame, Integer num, Integer num2) {
                VideoDecoderWrapper.nativeOnDecodedFrame(j, videoFrame, num, num2);
            }
        };
    }
}
