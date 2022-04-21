package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda98 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda98(MessagesController messagesController, TLRPC.messages_Dialogs messages_dialogs, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m312xface84d(this.f$1, this.f$2);
    }
}
