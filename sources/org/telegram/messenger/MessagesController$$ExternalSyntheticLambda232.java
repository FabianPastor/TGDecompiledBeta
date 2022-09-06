package org.telegram.messenger;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda232 implements RequestDelegate {
    public final /* synthetic */ MessagesController.IsInChatCheckedCallback f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda232(MessagesController.IsInChatCheckedCallback isInChatCheckedCallback) {
        this.f$0 = isInChatCheckedCallback;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$checkIsInChat$360(this.f$0, tLObject, tLRPC$TL_error);
    }
}
