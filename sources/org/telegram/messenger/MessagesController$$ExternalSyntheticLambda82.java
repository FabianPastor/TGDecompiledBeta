package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda82 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda82(MessagesController messagesController, TLRPC.messages_Dialogs messages_dialogs, int i) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m286xbae57e8(this.f$1, this.f$2);
    }
}
