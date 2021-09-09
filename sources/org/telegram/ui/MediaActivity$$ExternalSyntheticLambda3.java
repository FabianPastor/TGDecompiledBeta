package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class MediaActivity$$ExternalSyntheticLambda3 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ MediaActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MediaActivity$$ExternalSyntheticLambda3(MediaActivity mediaActivity, int i) {
        this.f$0 = mediaActivity;
        this.f$1 = i;
    }

    public final void didSetColor() {
        this.f$0.lambda$getThemeDescriptions$5(this.f$1);
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
