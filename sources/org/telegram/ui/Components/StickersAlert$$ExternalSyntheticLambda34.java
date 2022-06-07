package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda34 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ StickersAlert f$0;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda34(StickersAlert stickersAlert) {
        this.f$0 = stickersAlert;
    }

    public final void didSetColor() {
        this.f$0.updateColors();
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
