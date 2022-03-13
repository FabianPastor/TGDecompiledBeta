package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_help_promoData;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda82 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_help_promoData f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda82(MessagesController messagesController, long j, TLRPC$TL_help_promoData tLRPC$TL_help_promoData, int i) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_help_promoData;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$checkPromoInfoInternal$134(this.f$1, this.f$2, this.f$3);
    }
}
