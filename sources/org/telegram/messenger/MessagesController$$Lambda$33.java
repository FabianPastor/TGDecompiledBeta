package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$33 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final BaseFragment arg$3;
    private final TL_messages_editChatAdmin arg$4;

    MessagesController$$Lambda$33(MessagesController messagesController, int i, BaseFragment baseFragment, TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = baseFragment;
        this.arg$4 = tL_messages_editChatAdmin;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$setUserAdminRole$51$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
