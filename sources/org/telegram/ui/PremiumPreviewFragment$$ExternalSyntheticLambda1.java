package org.telegram.ui;

import androidx.core.util.Consumer;
import com.android.billingclient.api.BillingResult;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda1 implements Consumer {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda1(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void accept(Object obj) {
        PremiumPreviewFragment.lambda$buyPremium$4(this.f$0, (BillingResult) obj);
    }
}
