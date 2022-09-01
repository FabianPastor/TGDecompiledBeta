package org.telegram.ui;

import org.telegram.ui.ActionBar.ThemeDescription;

public final /* synthetic */ class ChatReactionsEditActivity$$ExternalSyntheticLambda4 implements ThemeDescription.ThemeDescriptionDelegate {
    public final /* synthetic */ ChatReactionsEditActivity f$0;

    public /* synthetic */ ChatReactionsEditActivity$$ExternalSyntheticLambda4(ChatReactionsEditActivity chatReactionsEditActivity) {
        this.f$0 = chatReactionsEditActivity;
    }

    public final void didSetColor() {
        this.f$0.updateColors();
    }

    public /* synthetic */ void onAnimationProgress(float f) {
        ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
    }
}
