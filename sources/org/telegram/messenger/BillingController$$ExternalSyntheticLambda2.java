package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetailsResponseListener;
import java.util.List;
/* loaded from: classes.dex */
public final /* synthetic */ class BillingController$$ExternalSyntheticLambda2 implements ProductDetailsResponseListener {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda2 INSTANCE = new BillingController$$ExternalSyntheticLambda2();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda2() {
    }

    @Override // com.android.billingclient.api.ProductDetailsResponseListener
    public final void onProductDetailsResponse(BillingResult billingResult, List list) {
        BillingController.lambda$onBillingSetupFinished$6(billingResult, list);
    }
}
