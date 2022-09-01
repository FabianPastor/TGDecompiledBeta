package org.telegram.ui;

import com.android.billingclient.api.BillingResult;
import java.util.List;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ BillingResult f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ PremiumPreviewFragment.SubscriptionTier f$3;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda5(BillingResult billingResult, BaseFragment baseFragment, List list, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        this.f$0 = billingResult;
        this.f$1 = baseFragment;
        this.f$2 = list;
        this.f$3 = subscriptionTier;
    }

    public final void run() {
        PremiumPreviewFragment.lambda$buyPremium$9(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
