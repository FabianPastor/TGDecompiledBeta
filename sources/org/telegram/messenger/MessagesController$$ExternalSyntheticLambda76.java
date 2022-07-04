package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_messages_createChat f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda76(MessagesController messagesController, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_createChat tL_messages_createChat) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_createChat;
    }

    public final void run() {
        this.f$0.m168lambda$createChat$207$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3);
    }
}
