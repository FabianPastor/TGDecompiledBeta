package org.telegram.ui;

import com.android.billingclient.api.BillingResult;
import java.util.List;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ BillingResult f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ List f$2;
    public final /* synthetic */ List f$3;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda3(BillingResult billingResult, BaseFragment baseFragment, List list, List list2) {
        this.f$0 = billingResult;
        this.f$1 = baseFragment;
        this.f$2 = list;
        this.f$3 = list2;
    }

    public final void run() {
        PremiumPreviewFragment.lambda$buyPremium$10(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
