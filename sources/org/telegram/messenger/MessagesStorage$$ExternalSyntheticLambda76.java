package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.messages_Dialogs f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda76(MessagesStorage messagesStorage, TLRPC.messages_Dialogs messages_dialogs, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m1023lambda$putDialogs$183$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2);
    }
}
