package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$N9vCK0oGVwwS8iSsLW2lhMHnKHI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ messages_Dialogs f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesController$N9vCK0oGVwwS8iSsLW2lhMHnKHI(MessagesController messagesController, messages_Dialogs messages_dialogs, int i) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$null$124$MessagesController(this.f$1, this.f$2);
    }
}
