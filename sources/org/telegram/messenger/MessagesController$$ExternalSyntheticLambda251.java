package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda251 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda251(MessagesController messagesController, int i, String str, String str2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkPromoInfoInternal$129(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
