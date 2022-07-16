package org.telegram.messenger;

import android.app.Activity;
import java.util.List;
import org.telegram.tgnet.TLRPC$InputStorePaymentPurpose;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ Activity f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ TLRPC$InputStorePaymentPurpose f$3;
    public final /* synthetic */ List f$4;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda5(BillingController billingController, Activity activity, AccountInstance accountInstance, TLRPC$InputStorePaymentPurpose tLRPC$InputStorePaymentPurpose, List list) {
        this.f$0 = billingController;
        this.f$1 = activity;
        this.f$2 = accountInstance;
        this.f$3 = tLRPC$InputStorePaymentPurpose;
        this.f$4 = list;
    }

    public final void run() {
        this.f$0.lambda$launchBillingFlow$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
