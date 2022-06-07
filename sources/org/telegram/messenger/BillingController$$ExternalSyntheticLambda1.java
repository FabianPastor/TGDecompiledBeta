package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetailsResponseListener;
import java.util.List;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda1 implements ProductDetailsResponseListener {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda1 INSTANCE = new BillingController$$ExternalSyntheticLambda1();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda1() {
    }

    public final void onProductDetailsResponse(BillingResult billingResult, List list) {
        BillingController.lambda$onBillingSetupFinished$4(billingResult, list);
    }
}
