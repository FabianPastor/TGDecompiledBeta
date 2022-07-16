package org.telegram.ui;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesResponseListener;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda2 implements PurchasesResponseListener {
    public final /* synthetic */ BaseFragment f$0;
    public final /* synthetic */ List f$1;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda2(BaseFragment baseFragment, List list) {
        this.f$0 = baseFragment;
        this.f$1 = list;
    }

    public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
        AndroidUtilities.runOnUIThread(new PremiumPreviewFragment$$ExternalSyntheticLambda3(billingResult, this.f$0, list, this.f$1));
    }
}
