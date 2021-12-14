package org.telegram.messenger;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda21 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ MessageObject f$5;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda21(MediaController mediaController, int i, int i2, long j, MessageObject messageObject, MessageObject messageObject2) {
        this.f$0 = mediaController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = messageObject;
        this.f$5 = messageObject2;
    }

    public final void run() {
        this.f$0.lambda$startRecording$26(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
