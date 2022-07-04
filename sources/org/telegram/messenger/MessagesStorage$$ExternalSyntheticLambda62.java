package org.telegram.messenger;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda62 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda62(MessagesStorage messagesStorage, TLObject tLObject, String str) {
        this.f$0 = messagesStorage;
        this.f$1 = tLObject;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.m2266lambda$saveBotCache$95$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2);
    }
}
