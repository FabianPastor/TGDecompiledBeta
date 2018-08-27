package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$92 implements RequestDelegate {
    private final MessagesController arg$1;
    private final BaseFragment arg$2;
    private final TL_channels_createChannel arg$3;

    MessagesController$$Lambda$92(MessagesController messagesController, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        this.arg$1 = messagesController;
        this.arg$2 = baseFragment;
        this.arg$3 = tL_channels_createChannel;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$createChat$139$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
