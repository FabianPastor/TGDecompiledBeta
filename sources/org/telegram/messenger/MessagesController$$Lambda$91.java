package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$91 implements RequestDelegate {
    private final MessagesController arg$1;
    private final BaseFragment arg$2;
    private final TL_messages_createChat arg$3;

    MessagesController$$Lambda$91(MessagesController messagesController, BaseFragment baseFragment, TL_messages_createChat tL_messages_createChat) {
        this.arg$1 = messagesController;
        this.arg$2 = baseFragment;
        this.arg$3 = tL_messages_createChat;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$createChat$136$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
