package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda219 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC.TL_messages_editChatAdmin f$4;
    public final /* synthetic */ MessagesController.ErrorDelegate f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda219(MessagesController messagesController, long j, Runnable runnable, BaseFragment baseFragment, TLRPC.TL_messages_editChatAdmin tL_messages_editChatAdmin, MessagesController.ErrorDelegate errorDelegate) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = runnable;
        this.f$3 = baseFragment;
        this.f$4 = tL_messages_editChatAdmin;
        this.f$5 = errorDelegate;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m408xd6cb678e(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
