package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda59 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC$TL_messages_editMessage f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda59(SendMessagesHelper sendMessagesHelper, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = baseFragment;
        this.f$3 = tLRPC$TL_messages_editMessage;
    }

    public final void run() {
        this.f$0.lambda$editMessage$15(this.f$1, this.f$2, this.f$3);
    }
}
