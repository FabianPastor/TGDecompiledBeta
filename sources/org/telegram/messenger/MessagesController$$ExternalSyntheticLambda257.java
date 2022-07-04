package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda257 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC.TL_channels_inviteToChannel f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda257(MessagesController messagesController, BaseFragment baseFragment, TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tL_channels_inviteToChannel;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m126xdd488535(this.f$1, this.f$2, tLObject, tL_error);
    }
}
