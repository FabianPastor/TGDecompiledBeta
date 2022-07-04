package org.webrtc;

public interface VideoSink {
    void onFrame(VideoFrame videoFrame);

    void setParentSink(VideoSink videoSink);

    /* renamed from: org.webrtc.VideoSink$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static void $default$setParentSink(VideoSink _this, VideoSink parent) {
        }
    }
}
