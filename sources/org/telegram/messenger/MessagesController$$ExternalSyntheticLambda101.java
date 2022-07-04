package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda101 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ long[] f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda101(MessagesController messagesController, TLRPC.messages_Dialogs messages_dialogs, int i, boolean z, long[] jArr, int i2) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = jArr;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.m323xCLASSNAMEadc1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
