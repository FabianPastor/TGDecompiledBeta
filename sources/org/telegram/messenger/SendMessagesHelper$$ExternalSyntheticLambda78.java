package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda78 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda78(SendMessagesHelper sendMessagesHelper) {
        this.f$0 = sendMessagesHelper;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendReaction$22(tLObject, tLRPC$TL_error);
    }
}
