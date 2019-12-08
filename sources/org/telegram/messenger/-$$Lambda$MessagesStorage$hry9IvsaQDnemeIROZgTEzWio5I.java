package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$hry9IvsaQDnemeIROZgTEzWio5I implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ messages_Dialogs f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$hry9IvsaQDnemeIROZgTEzWio5I(MessagesStorage messagesStorage, messages_Dialogs messages_dialogs, int i) {
        this.f$0 = messagesStorage;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$putDialogs$148$MessagesStorage(this.f$1, this.f$2);
    }
}
