package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda152 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda152(MessagesController messagesController, TLRPC$ChatFull tLRPC$ChatFull, String str) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$updateChatAbout$237(this.f$1, this.f$2);
    }
}
