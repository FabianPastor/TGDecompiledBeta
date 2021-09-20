package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda8 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda8(SharedMediaLayout sharedMediaLayout, int i) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = i;
    }

    public final void didSetColor() {
        this.f$0.lambda$getThemeDescriptions$12(this.f$1);
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
