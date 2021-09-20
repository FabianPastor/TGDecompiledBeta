package org.webrtc;

import org.webrtc.VideoDecoder;

public final /* synthetic */ class VideoDecoderWrapper$$ExternalSyntheticLambda0 implements VideoDecoder.Callback {
    public final /* synthetic */ long f$0;

    public /* synthetic */ VideoDecoderWrapper$$ExternalSyntheticLambda0(long j) {
        this.f$0 = j;
    }

    public final void onDecodedFrame(VideoFrame videoFrame, Integer num, Integer num2) {
        VideoDecoderWrapper.nativeOnDecodedFrame(this.f$0, videoFrame, num, num2);
    }
}
