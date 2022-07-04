package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda256 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC.TL_channels_createChannel f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda256(MessagesController messagesController, BaseFragment baseFragment, TLRPC.TL_channels_createChannel tL_channels_createChannel) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tL_channels_createChannel;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m173lambda$createChat$212$orgtelegrammessengerMessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
