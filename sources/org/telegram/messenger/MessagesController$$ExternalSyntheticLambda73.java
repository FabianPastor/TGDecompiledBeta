package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_updateServiceNotification f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda73(MessagesController messagesController, TLRPC.TL_updateServiceNotification tL_updateServiceNotification) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateServiceNotification;
    }

    public final void run() {
        this.f$0.m326xfvar_e6b(this.f$1);
    }
}
