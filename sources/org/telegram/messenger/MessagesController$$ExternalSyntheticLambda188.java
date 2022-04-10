package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda188 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda188(MessagesController messagesController, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdate$185(this.f$1, this.f$2);
    }
}
