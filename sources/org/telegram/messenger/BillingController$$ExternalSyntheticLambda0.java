package org.telegram.messenger;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingResult;
import java.util.List;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda0 implements AcknowledgePurchaseResponseListener {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda0(BillingController billingController, List list, String str) {
        this.f$0 = billingController;
        this.f$1 = list;
        this.f$2 = str;
    }

    public final void onAcknowledgePurchaseResponse(BillingResult billingResult) {
        this.f$0.lambda$acknowledgePurchase$1(this.f$1, this.f$2, billingResult);
    }
}
