package org.telegram.messenger.voip;

public final /* synthetic */ class NativeInstance$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ NativeInstance f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int[] f$2;

    public /* synthetic */ NativeInstance$$ExternalSyntheticLambda1(NativeInstance nativeInstance, long j, int[] iArr) {
        this.f$0 = nativeInstance;
        this.f$1 = j;
        this.f$2 = iArr;
    }

    public final void run() {
        this.f$0.lambda$onParticipantDescriptionsRequired$2(this.f$1, this.f$2);
    }
}