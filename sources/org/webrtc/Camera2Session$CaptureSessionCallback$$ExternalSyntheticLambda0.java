package org.webrtc;

import org.webrtc.Camera2Session;
import org.webrtc.VideoSink;

public final /* synthetic */ class Camera2Session$CaptureSessionCallback$$ExternalSyntheticLambda0 implements VideoSink {
    public final /* synthetic */ Camera2Session.CaptureSessionCallback f$0;

    public /* synthetic */ Camera2Session$CaptureSessionCallback$$ExternalSyntheticLambda0(Camera2Session.CaptureSessionCallback captureSessionCallback) {
        this.f$0 = captureSessionCallback;
    }

    public final void onFrame(VideoFrame videoFrame) {
        this.f$0.m4611x8bd2b057(videoFrame);
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }
}
