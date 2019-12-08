package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xvar_b072yu01cRpjctPtalY7nds implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_messages_createChat f$3;

    public /* synthetic */ -$$Lambda$MessagesController$xvar_b072yu01cRpjctPtalY7nds(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_createChat;
    }

    public final void run() {
        this.f$0.lambda$null$157$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
