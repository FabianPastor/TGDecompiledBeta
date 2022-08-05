package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda90 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC$TL_messages_editMessage f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda90(SendMessagesHelper sendMessagesHelper, BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = baseFragment;
        this.f$2 = tLRPC$TL_messages_editMessage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$editMessage$16(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
