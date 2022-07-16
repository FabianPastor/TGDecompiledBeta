package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_assignPlayMarketTransaction;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ Purchase f$2;
    public final /* synthetic */ BillingResult f$3;
    public final /* synthetic */ TLRPC$TL_payments_assignPlayMarketTransaction f$4;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda7(BillingController billingController, AccountInstance accountInstance, Purchase purchase, BillingResult billingResult, TLRPC$TL_payments_assignPlayMarketTransaction tLRPC$TL_payments_assignPlayMarketTransaction) {
        this.f$0 = billingController;
        this.f$1 = accountInstance;
        this.f$2 = purchase;
        this.f$3 = billingResult;
        this.f$4 = tLRPC$TL_payments_assignPlayMarketTransaction;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onPurchasesUpdated$4(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
