package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$ThemeInfo$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Theme.ThemeInfo f$0;

    public /* synthetic */ Theme$ThemeInfo$$ExternalSyntheticLambda0(Theme.ThemeInfo themeInfo) {
        this.f$0 = themeInfo;
    }

    public final void run() {
        this.f$0.onFinishLoadingRemoteTheme();
    }
}
