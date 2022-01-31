package org.telegram.messenger;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ byte[] f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda35(MessagesStorage messagesStorage, int i, int i2, byte[] bArr) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.lambda$saveSecretParams$8(this.f$1, this.f$2, this.f$3);
    }
}
