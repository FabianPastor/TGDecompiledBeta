package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesResponseListener;
import java.util.List;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda2 implements PurchasesResponseListener {
    public final /* synthetic */ BillingController f$0;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda2(BillingController billingController) {
        this.f$0 = billingController;
    }

    public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
        this.f$0.onPurchasesUpdated(billingResult, list);
    }
}
