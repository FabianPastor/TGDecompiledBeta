package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda150 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda150(MessagesStorage messagesStorage, TLObject tLObject, String str) {
        this.f$0 = messagesStorage;
        this.f$1 = tLObject;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$saveBotCache$90(this.f$1, this.f$2);
    }
}
