package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda81 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda81(SendMessagesHelper sendMessagesHelper, String str) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendNotificationCallback$18(this.f$1, tLObject, tLRPC$TL_error);
    }
}
