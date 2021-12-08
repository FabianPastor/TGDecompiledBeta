package org.telegram.messenger;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ MediaController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ byte[] f$2;
    public final /* synthetic */ MessageObject f$3;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda26(MediaController mediaController, String str, byte[] bArr, MessageObject messageObject) {
        this.f$0 = mediaController;
        this.f$1 = str;
        this.f$2 = bArr;
        this.f$3 = messageObject;
    }

    public final void run() {
        this.f$0.lambda$generateWaveform$27(this.f$1, this.f$2, this.f$3);
    }
}
