package org.telegram.ui.Components.Premium;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetailsResponseListener;
import java.util.List;

public final /* synthetic */ class GiftPremiumBottomSheet$$ExternalSyntheticLambda4 implements ProductDetailsResponseListener {
    public final /* synthetic */ GiftPremiumBottomSheet f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ GiftPremiumBottomSheet$$ExternalSyntheticLambda4(GiftPremiumBottomSheet giftPremiumBottomSheet, long j) {
        this.f$0 = giftPremiumBottomSheet;
        this.f$1 = j;
    }

    public final void onProductDetailsResponse(BillingResult billingResult, List list) {
        this.f$0.lambda$new$1(this.f$1, billingResult, list);
    }
}
