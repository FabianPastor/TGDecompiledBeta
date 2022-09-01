package org.telegram.ui;

import org.telegram.messenger.GenericProvider;
import org.telegram.ui.Components.Premium.PremiumTierCell;
import org.telegram.ui.PremiumPreviewFragment;

public final /* synthetic */ class PremiumPreviewFragment$BackgroundView$3$$ExternalSyntheticLambda0 implements GenericProvider {
    public final /* synthetic */ PremiumPreviewFragment.BackgroundView.AnonymousClass3 f$0;
    public final /* synthetic */ PremiumTierCell f$1;

    public /* synthetic */ PremiumPreviewFragment$BackgroundView$3$$ExternalSyntheticLambda0(PremiumPreviewFragment.BackgroundView.AnonymousClass3 r1, PremiumTierCell premiumTierCell) {
        this.f$0 = r1;
        this.f$1 = premiumTierCell;
    }

    public final Object provide(Object obj) {
        return this.f$0.lambda$onCreateViewHolder$0(this.f$1, (Void) obj);
    }
}
