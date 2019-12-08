package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$D-D0ksFXWFGlKcR_fK1zs8UIoKQ implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_messages_createChat f$3;

    public /* synthetic */ -$$Lambda$MessagesController$D-D0ksFXWFGlKcR_fK1zs8UIoKQ(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_createChat;
    }

    public final void run() {
        this.f$0.lambda$null$159$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
