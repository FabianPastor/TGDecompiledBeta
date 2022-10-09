package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
/* loaded from: classes.dex */
public final /* synthetic */ class BillingController$$ExternalSyntheticLambda1 implements ConsumeResponseListener {
    public static final /* synthetic */ BillingController$$ExternalSyntheticLambda1 INSTANCE = new BillingController$$ExternalSyntheticLambda1();

    private /* synthetic */ BillingController$$ExternalSyntheticLambda1() {
    }

    @Override // com.android.billingclient.api.ConsumeResponseListener
    public final void onConsumeResponse(BillingResult billingResult, String str) {
        BillingController.lambda$onPurchasesUpdated$3(billingResult, str);
    }
}
