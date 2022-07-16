package org.telegram.ui.Components.Premium;

import java.util.concurrent.atomic.AtomicReference;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;

public final /* synthetic */ class GiftPremiumBottomSheet$1$$ExternalSyntheticLambda1 implements CheckBoxBase.ProgressDelegate {
    public final /* synthetic */ AtomicReference f$0;
    public final /* synthetic */ PremiumGiftTierCell f$1;

    public /* synthetic */ GiftPremiumBottomSheet$1$$ExternalSyntheticLambda1(AtomicReference atomicReference, PremiumGiftTierCell premiumGiftTierCell) {
        this.f$0 = atomicReference;
        this.f$1 = premiumGiftTierCell;
    }

    public final void setProgress(float f) {
        GiftPremiumBottomSheet.AnonymousClass1.lambda$onCreateViewHolder$1(this.f$0, this.f$1, f);
    }
}
