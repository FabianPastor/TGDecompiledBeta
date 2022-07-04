package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ Purchase f$2;
    public final /* synthetic */ BillingResult f$3;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda3(BillingController billingController, AccountInstance accountInstance, Purchase purchase, BillingResult billingResult) {
        this.f$0 = billingController;
        this.f$1 = accountInstance;
        this.f$2 = purchase;
        this.f$3 = billingResult;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1xaefb09cd(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
