package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updateServiceNotification;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda182 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_updateServiceNotification f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda182(MessagesController messagesController, TLRPC$TL_updateServiceNotification tLRPC$TL_updateServiceNotification) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_updateServiceNotification;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$315(this.f$1);
    }
}
