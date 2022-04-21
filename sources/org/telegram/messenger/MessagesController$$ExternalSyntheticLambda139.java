package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda139 implements RequestDelegate {
    public final /* synthetic */ MessagesController.IsInChatCheckedCallback f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda139(MessagesController.IsInChatCheckedCallback isInChatCheckedCallback) {
        this.f$0 = isInChatCheckedCallback;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$checkIsInChat$351(this.f$0, tLObject, tL_error);
    }
}
