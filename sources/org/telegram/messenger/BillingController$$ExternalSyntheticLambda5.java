package org.telegram.messenger;

import com.android.billingclient.api.Purchase;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ Purchase f$2;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda5(BillingController billingController, AccountInstance accountInstance, Purchase purchase) {
        this.f$0 = billingController;
        this.f$1 = accountInstance;
        this.f$2 = purchase;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onPurchasesUpdated$2(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
