package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda185 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ long[] f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda185(MessagesController messagesController, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i, boolean z, long[] jArr, int i2) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = jArr;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDialogs$173(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
