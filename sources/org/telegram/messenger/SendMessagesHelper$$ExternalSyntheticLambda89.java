package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda89 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$InputMedia f$1;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda89(SendMessagesHelper sendMessagesHelper, TLRPC$InputMedia tLRPC$InputMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$InputMedia;
        this.f$2 = delayedMessage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$uploadMultiMedia$35(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
