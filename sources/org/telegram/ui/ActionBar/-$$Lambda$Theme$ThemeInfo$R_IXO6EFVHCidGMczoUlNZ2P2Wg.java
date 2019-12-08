package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$ThemeInfo$R_IXO6EFVHCidGMczoUlNZ2P2Wg implements Runnable {
    private final /* synthetic */ ThemeInfo f$0;

    public /* synthetic */ -$$Lambda$Theme$ThemeInfo$R_IXO6EFVHCidGMczoUlNZ2P2Wg(ThemeInfo themeInfo) {
        this.f$0 = themeInfo;
    }

    public final void run() {
        this.f$0.onFinishLoadingRemoteTheme();
    }
}
