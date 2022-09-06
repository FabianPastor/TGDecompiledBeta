package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda191 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda191(MessagesController messagesController, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$migrateDialogs$173(this.f$1, this.f$2);
    }
}
