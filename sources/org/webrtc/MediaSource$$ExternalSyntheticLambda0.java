package org.webrtc;

public final /* synthetic */ class MediaSource$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ long f$0;

    public /* synthetic */ MediaSource$$ExternalSyntheticLambda0(long j) {
        this.f$0 = j;
    }

    public final void run() {
        JniCommon.nativeReleaseRef(this.f$0);
    }
}
