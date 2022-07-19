package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_help_premiumPromo;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda100 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_help_premiumPromo f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda100(MediaDataController mediaDataController, TLRPC$TL_help_premiumPromo tLRPC$TL_help_premiumPromo, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_help_premiumPromo;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$putPremiumPromoToCache$9(this.f$1, this.f$2);
    }
}
