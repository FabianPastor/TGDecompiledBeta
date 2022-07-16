package org.telegram.messenger;

import android.app.Activity;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesResponseListener;
import java.util.List;
import org.telegram.tgnet.TLRPC$InputStorePaymentPurpose;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda4 implements PurchasesResponseListener {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ Activity f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ TLRPC$InputStorePaymentPurpose f$3;
    public final /* synthetic */ List f$4;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda4(BillingController billingController, Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List list) {
        this.f$0 = billingController;
        this.f$1 = activity;
        this.f$2 = accountInstance;
        this.f$3 = tLRPC$InputStorePaymentPurpose;
        this.f$4 = list;
    }

    public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
        this.f$0.lambda$launchBillingFlow$2(this.f$1, this.f$2, this.f$3, this.f$4, billingResult, list);
    }
}
