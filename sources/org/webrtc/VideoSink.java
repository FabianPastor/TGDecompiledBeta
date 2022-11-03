package org.webrtc;
/* loaded from: classes.dex */
public interface VideoSink {

    /* renamed from: org.webrtc.VideoSink$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static void $default$setParentSink(VideoSink videoSink, VideoSink videoSink2) {
        }
    }

    @CalledByNative
    void onFrame(VideoFrame videoFrame);

    void setParentSink(VideoSink videoSink);
}
