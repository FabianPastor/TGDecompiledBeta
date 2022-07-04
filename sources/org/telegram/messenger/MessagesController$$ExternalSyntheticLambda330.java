package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_channels_createChannel;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda330 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC$TL_channels_createChannel f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda330(MessagesController messagesController, BaseFragment baseFragment, TLRPC$TL_channels_createChannel tLRPC$TL_channels_createChannel) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tLRPC$TL_channels_createChannel;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createChat$212(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
