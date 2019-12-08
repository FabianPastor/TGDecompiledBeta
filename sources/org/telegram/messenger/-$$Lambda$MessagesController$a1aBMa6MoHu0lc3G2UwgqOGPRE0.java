package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$a1aBMa6MoHu0lc3G2UwgqOGPRE0 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ messages_Dialogs f$1;

    public /* synthetic */ -$$Lambda$MessagesController$a1aBMa6MoHu0lc3G2UwgqOGPRE0(MessagesController messagesController, messages_Dialogs messages_dialogs) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdate$154$MessagesController(this.f$1);
    }
}
