package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.ActionBarLayout;

public final /* synthetic */ class ActionBarLayout$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ActionBarLayout f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ActionBarLayout.ThemeAnimationSettings f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ ActionBarLayout$$ExternalSyntheticLambda3(ActionBarLayout actionBarLayout, int i, ActionBarLayout.ThemeAnimationSettings themeAnimationSettings, Runnable runnable) {
        this.f$0 = actionBarLayout;
        this.f$1 = i;
        this.f$2 = themeAnimationSettings;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$animateThemedValues$6(this.f$1, this.f$2, this.f$3);
    }
}
