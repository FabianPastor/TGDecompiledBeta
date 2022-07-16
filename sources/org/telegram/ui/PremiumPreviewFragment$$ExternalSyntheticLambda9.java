package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ TLRPC$TL_payments_assignPlayMarketTransaction f$2;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda9(BaseFragment baseFragment, Runnable runnable, TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction) {
        this.f$0 = baseFragment;
        this.f$1 = runnable;
        this.f$2 = tLRPC$TL_payments_assignPlayMarketTransaction;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PremiumPreviewFragment.lambda$buyPremium$6(this.f$0, this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
