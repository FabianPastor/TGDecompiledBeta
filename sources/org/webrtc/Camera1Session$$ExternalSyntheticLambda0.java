package org.webrtc;

import org.webrtc.VideoSink;

public final /* synthetic */ class Camera1Session$$ExternalSyntheticLambda0 implements VideoSink {
    public final /* synthetic */ Camera1Session f$0;

    public /* synthetic */ Camera1Session$$ExternalSyntheticLambda0(Camera1Session camera1Session) {
        this.f$0 = camera1Session;
    }

    public final void onFrame(VideoFrame videoFrame) {
        this.f$0.m4608lambda$listenForTextureFrames$0$orgwebrtcCamera1Session(videoFrame);
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }
}
