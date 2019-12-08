package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$TnYN7DB0fVLoGs8fBZ2DYqXHSLU implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_updateServiceNotification f$1;

    public /* synthetic */ -$$Lambda$MessagesController$TnYN7DB0fVLoGs8fBZ2DYqXHSLU(MessagesController messagesController, TL_updateServiceNotification tL_updateServiceNotification) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateServiceNotification;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$259$MessagesController(this.f$1);
    }
}
