package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class LoginActivity$$ExternalSyntheticLambda22 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ LoginActivity f$0;

    public /* synthetic */ LoginActivity$$ExternalSyntheticLambda22(LoginActivity loginActivity) {
        this.f$0 = loginActivity;
    }

    public final void didSetColor() {
        this.f$0.updateColors();
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
