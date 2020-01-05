package org.telegram.ui.ActionBar;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$xUyl4Wlvar_OIslQJNxBDYIy9o8 implements Runnable {
    private final /* synthetic */ TLObject f$0;
    private final /* synthetic */ TL_theme f$1;
    private final /* synthetic */ ThemeAccent f$2;
    private final /* synthetic */ ThemeInfo f$3;

    public /* synthetic */ -$$Lambda$Theme$xUyl4Wlvar_OIslQJNxBDYIy9o8(TLObject tLObject, TL_theme tL_theme, ThemeAccent themeAccent, ThemeInfo themeInfo) {
        this.f$0 = tLObject;
        this.f$1 = tL_theme;
        this.f$2 = themeAccent;
        this.f$3 = themeInfo;
    }

    public final void run() {
        Theme.lambda$null$3(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
