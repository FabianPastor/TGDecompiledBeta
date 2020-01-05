package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$5X-GQzTUsL_TJG4ZKnsInj8fCJ4 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ long[] f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$5X-GQzTUsL_TJG4ZKnsInj8fCJ4(MessagesStorage messagesStorage, int i, int i2, int i3, long[] jArr) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = jArr;
    }

    public final void run() {
        this.f$0.lambda$getDialogs$136$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
