package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editChatAdmin;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda292 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC$TL_messages_editChatAdmin f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda292(MessagesController messagesController, long j, Runnable runnable, BaseFragment baseFragment, TLRPC$TL_messages_editChatAdmin tLRPC$TL_messages_editChatAdmin, Runnable runnable2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = runnable;
        this.f$3 = baseFragment;
        this.f$4 = tLRPC$TL_messages_editChatAdmin;
        this.f$5 = runnable2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$setUserAdminRole$83(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
