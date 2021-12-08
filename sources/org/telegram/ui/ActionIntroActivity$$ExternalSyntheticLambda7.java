package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class ActionIntroActivity$$ExternalSyntheticLambda7 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ ActionIntroActivity f$0;

    public /* synthetic */ ActionIntroActivity$$ExternalSyntheticLambda7(ActionIntroActivity actionIntroActivity) {
        this.f$0 = actionIntroActivity;
    }

    public final void didSetColor() {
        this.f$0.updateColors();
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
