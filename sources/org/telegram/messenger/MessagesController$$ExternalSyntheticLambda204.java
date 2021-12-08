package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda204 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda204(MessagesController messagesController, long j, BaseFragment baseFragment, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = baseFragment;
        this.f$3 = tL_messages_editChatAdmin;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m393x13defe2f(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
