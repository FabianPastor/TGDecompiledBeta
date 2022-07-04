package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda178 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda178(MessagesStorage messagesStorage, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$putDialogs$191(this.f$1, this.f$2);
    }
}
