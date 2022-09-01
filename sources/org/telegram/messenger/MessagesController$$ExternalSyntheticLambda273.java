package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_promoData;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda273 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_help_promoData f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda273(MessagesController messagesController, int i, TLRPC$TL_help_promoData tLRPC$TL_help_promoData, long j) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_help_promoData;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkPromoInfoInternal$137(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
