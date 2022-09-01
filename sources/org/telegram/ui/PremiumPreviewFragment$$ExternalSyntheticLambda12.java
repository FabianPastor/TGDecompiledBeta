package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_canPurchasePremium;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ PremiumPreviewFragment.SubscriptionTier f$1;
    public final /* synthetic */ TLRPC$TL_payments_canPurchasePremium f$2;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda12(BaseFragment baseFragment, PremiumPreviewFragment.SubscriptionTier subscriptionTier, TLRPC$TL_payments_canPurchasePremium tLRPC$TL_payments_canPurchasePremium) {
        this.f$0 = baseFragment;
        this.f$1 = subscriptionTier;
        this.f$2 = tLRPC$TL_payments_canPurchasePremium;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda6(tLObject, this.f$0, this.f$1, tLRPC$TL_error, this.f$2));
    }
}
