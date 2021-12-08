package org.webrtc;

import org.webrtc.VideoFrame;

public final /* synthetic */ class JavaI420Buffer$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ VideoFrame.I420Buffer f$0;

    public /* synthetic */ JavaI420Buffer$$ExternalSyntheticLambda1(VideoFrame.I420Buffer i420Buffer) {
        this.f$0 = i420Buffer;
    }

    public final void run() {
        this.f$0.release();
    }
}
