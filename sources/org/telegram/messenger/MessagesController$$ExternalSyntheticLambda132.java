package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda132 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda132(MessagesController messagesController, TLRPC$ChatFull tLRPC$ChatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$updateChatAbout$218(this.f$1, this.f$2);
    }
}
