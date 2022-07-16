package org.telegram.messenger;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final /* synthetic */ class BillingController$$ExternalSyntheticLambda0 implements ConsumeResponseListener {
    public final /* synthetic */ List f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ AtomicInteger f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ BillingController$$ExternalSyntheticLambda0(List list, String str, AtomicInteger atomicInteger, Runnable runnable) {
        this.f$0 = list;
        this.f$1 = str;
        this.f$2 = atomicInteger;
        this.f$3 = runnable;
    }

    public final void onConsumeResponse(BillingResult billingResult, String str) {
        BillingController.lambda$launchBillingFlow$1(this.f$0, this.f$1, this.f$2, this.f$3, billingResult, str);
    }
}
