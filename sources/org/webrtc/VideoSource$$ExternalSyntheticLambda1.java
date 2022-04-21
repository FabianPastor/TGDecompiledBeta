package org.webrtc;

import org.webrtc.VideoSink;

public final /* synthetic */ class VideoSource$$ExternalSyntheticLambda1 implements VideoSink {
    public final /* synthetic */ VideoSource f$0;

    public /* synthetic */ VideoSource$$ExternalSyntheticLambda1(VideoSource videoSource) {
        this.f$0 = videoSource;
    }

    public final void onFrame(VideoFrame videoFrame) {
        this.f$0.m4643lambda$setVideoProcessor$1$orgwebrtcVideoSource(videoFrame);
    }

    public /* synthetic */ void setParentSink(VideoSink videoSink) {
        VideoSink.CC.$default$setParentSink(this, videoSink);
    }
}
