package org.telegram.messenger;

import java.util.List;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ BillingController f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda3(BillingController billingController, List list, String str) {
        this.f$0 = billingController;
        this.f$1 = list;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$acknowledgePurchase$0(this.f$1, this.f$2);
    }
}
