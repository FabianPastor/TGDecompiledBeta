package org.telegram.ui;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesResponseListener;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda4 implements PurchasesResponseListener {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ PremiumPreviewFragment.SubscriptionTier f$1;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda4(BaseFragment baseFragment, PremiumPreviewFragment.SubscriptionTier subscriptionTier) {
        this.f$0 = baseFragment;
        this.f$1 = subscriptionTier;
    }

    public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
        AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda5(billingResult, this.f$0, list, this.f$1));
    }
}
