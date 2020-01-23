package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$iQGqicF_sP3IboUHlQ-xhPJgu7I implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_channels_createChannel f$3;

    public /* synthetic */ -$$Lambda$MessagesController$iQGqicF_sP3IboUHlQ-xhPJgu7I(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_channels_createChannel;
    }

    public final void run() {
        this.f$0.lambda$null$175$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
