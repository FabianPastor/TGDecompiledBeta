package org.telegram.ui;

import java.util.List;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TLObject f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ TLRPC$TL_payments_canPurchasePremium f$4;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda2(TLObject tLObject, BaseFragment baseFragment, List list, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        this.f$0 = tLObject;
        this.f$1 = baseFragment;
        this.f$2 = list;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = tLRPC$TL_payments_canPurchasePremium;
    }

    public final void run() {
        PremiumPreviewFragment.lambda$buyPremium$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}