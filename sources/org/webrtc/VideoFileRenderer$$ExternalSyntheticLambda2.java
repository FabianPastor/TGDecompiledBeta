package org.webrtc;

import org.webrtc.VideoFrame;

public final /* synthetic */ class VideoFileRenderer$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ VideoFileRenderer f$0;
    public final /* synthetic */ VideoFrame.I420Buffer f$1;
    public final /* synthetic */ VideoFrame f$2;

    public /* synthetic */ VideoFileRenderer$$ExternalSyntheticLambda2(VideoFileRenderer videoFileRenderer, VideoFrame.I420Buffer i420Buffer, VideoFrame videoFrame) {
        this.f$0 = videoFileRenderer;
        this.f$1 = i420Buffer;
        this.f$2 = videoFrame;
    }

    public final void run() {
        this.f$0.m1674lambda$renderFrameOnRenderThread$1$orgwebrtcVideoFileRenderer(this.f$1, this.f$2);
    }
}
