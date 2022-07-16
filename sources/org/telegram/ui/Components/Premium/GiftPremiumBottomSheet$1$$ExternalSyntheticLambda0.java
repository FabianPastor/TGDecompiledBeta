package org.telegram.ui.Components.Premium;

import org.telegram.messenger.GenericProvider;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;

public final /* synthetic */ class GiftPremiumBottomSheet$1$$ExternalSyntheticLambda0 implements GenericProvider {
    public final /* synthetic */ GiftPremiumBottomSheet.AnonymousClass1 f$0;
    public final /* synthetic */ PremiumGiftTierCell f$1;

    public /* synthetic */ GiftPremiumBottomSheet$1$$ExternalSyntheticLambda0(GiftPremiumBottomSheet.AnonymousClass1 r1, PremiumGiftTierCell premiumGiftTierCell) {
        this.f$0 = r1;
        this.f$1 = premiumGiftTierCell;
    }

    public final Object provide(Object obj) {
        return this.f$0.lambda$onCreateViewHolder$0(this.f$1, (Void) obj);
    }
}
