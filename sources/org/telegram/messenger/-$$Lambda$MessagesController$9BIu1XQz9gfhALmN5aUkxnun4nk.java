package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$9BIu1XQz9gfhALmN5aUkxnun4nk implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ BaseFragment f$1;
    private final /* synthetic */ TL_channels_createChannel f$2;

    public /* synthetic */ -$$Lambda$MessagesController$9BIu1XQz9gfhALmN5aUkxnun4nk(MessagesController messagesController, BaseFragment baseFragment, TL_channels_createChannel tL_channels_createChannel) {
        this.f$0 = messagesController;
        this.f$1 = baseFragment;
        this.f$2 = tL_channels_createChannel;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$createChat$177$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
