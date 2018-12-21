package org.telegram.messenger;

import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$98 implements RequestDelegate {
    private final MessagesController arg$1;
    private final BaseFragment arg$2;
    private final TL_channels_inviteToChannel arg$3;

    MessagesController$$Lambda$98(MessagesController messagesController, BaseFragment baseFragment, TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        this.arg$1 = messagesController;
        this.arg$2 = baseFragment;
        this.arg$3 = tL_channels_inviteToChannel;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$addUsersToChannel$150$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
