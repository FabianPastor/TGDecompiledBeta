package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$30 implements RequestDelegate {
    private final MessagesController arg$1;
    private final int arg$2;
    private final BaseFragment arg$3;
    private final TL_messages_editChatDefaultBannedRights arg$4;
    private final boolean arg$5;

    MessagesController$$Lambda$30(MessagesController messagesController, int i, BaseFragment baseFragment, TL_messages_editChatDefaultBannedRights tL_messages_editChatDefaultBannedRights, boolean z) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = baseFragment;
        this.arg$4 = tL_messages_editChatDefaultBannedRights;
        this.arg$5 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$setDefaultBannedRole$43$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
