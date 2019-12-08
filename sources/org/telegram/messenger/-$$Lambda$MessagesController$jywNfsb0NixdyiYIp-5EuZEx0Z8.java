package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$jywNfsb0NixdyiYIp-5EuZEx0Z8 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_updateServiceNotification f$1;

    public /* synthetic */ -$$Lambda$MessagesController$jywNfsb0NixdyiYIp-5EuZEx0Z8(MessagesController messagesController, TL_updateServiceNotification tL_updateServiceNotification) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateServiceNotification;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$248$MessagesController(this.f$1);
    }
}
