package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$b5L6VRSs4PelftHC0ReXA3Sme3U implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ messages_Dialogs f$1;

    public /* synthetic */ -$$Lambda$MessagesController$b5L6VRSs4PelftHC0ReXA3Sme3U(MessagesController messagesController, messages_Dialogs messages_dialogs) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
    }

    public final void run() {
        this.f$0.lambda$processDialogsUpdate$140$MessagesController(this.f$1);
    }
}
