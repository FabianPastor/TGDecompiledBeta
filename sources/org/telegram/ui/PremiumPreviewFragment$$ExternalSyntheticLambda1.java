package org.telegram.ui;

import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingResult;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda1(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void accept(Object obj) {
        PremiumPreviewFragment.lambda$buyPremium$7(this.f$0, (BillingResult) obj);
    }
}
