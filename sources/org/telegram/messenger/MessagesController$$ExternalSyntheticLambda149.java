package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editChatDefaultBannedRights;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda149 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC$TL_messages_editChatDefaultBannedRights f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda149(MessagesController messagesController, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editChatDefaultBannedRights tLRPC$TL_messages_editChatDefaultBannedRights, boolean z) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = baseFragment;
        this.f$3 = tLRPC$TL_messages_editChatDefaultBannedRights;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$setDefaultBannedRole$69(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}