package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_config;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$kaUZtC5eFoI1yq1XLg5Vzbg2RzY implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_config f$1;

    public /* synthetic */ -$$Lambda$MessagesController$kaUZtC5eFoI1yq1XLg5Vzbg2RzY(MessagesController messagesController, TL_config tL_config) {
        this.f$0 = messagesController;
        this.f$1 = tL_config;
    }

    public final void run() {
        this.f$0.lambda$updateConfig$4$MessagesController(this.f$1);
    }
}
