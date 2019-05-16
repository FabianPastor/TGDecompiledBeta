package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$NtpBYQeXRJpUfal8CNuiE9yNR3Y implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ byte[] f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$NtpBYQeXRJpUfal8CNuiE9yNR3Y(MessagesStorage messagesStorage, int i, int i2, byte[] bArr) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = bArr;
    }

    public final void run() {
        this.f$0.lambda$saveSecretParams$4$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
