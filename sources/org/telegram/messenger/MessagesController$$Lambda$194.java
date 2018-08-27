package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$194 implements Runnable {
    private final MessagesController arg$1;
    private final TL_error arg$2;
    private final BaseFragment arg$3;
    private final TL_channels_createChannel arg$4;

    MessagesController$$Lambda$194(MessagesController messagesController, TL_error tL_error, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_error;
        this.arg$3 = baseFragment;
        this.arg$4 = tL_channels_createChannel;
    }

    public void run() {
        this.arg$1.lambda$null$137$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
