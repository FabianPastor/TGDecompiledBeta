package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda17 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ PremiumPreviewFragment f$0;

    public /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda17(PremiumPreviewFragment premiumPreviewFragment) {
        this.f$0 = premiumPreviewFragment;
    }

    public final void didSetColor() {
        this.f$0.updateColors();
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
