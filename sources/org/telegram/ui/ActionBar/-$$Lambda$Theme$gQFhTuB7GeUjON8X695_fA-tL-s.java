package org.telegram.ui.ActionBar;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$gQFhTuB7GeUjON8X695_fA-tL-s implements Runnable {
    private final /* synthetic */ TLObject f$0;
    private final /* synthetic */ ThemeAccent f$1;
    private final /* synthetic */ ThemeInfo f$2;
    private final /* synthetic */ TL_theme f$3;

    public /* synthetic */ -$$Lambda$Theme$gQFhTuB7GeUjON8X695_fA-tL-s(TLObject tLObject, ThemeAccent themeAccent, ThemeInfo themeInfo, TL_theme tL_theme) {
        this.f$0 = tLObject;
        this.f$1 = themeAccent;
        this.f$2 = themeInfo;
        this.f$3 = tL_theme;
    }

    public final void run() {
        Theme.lambda$null$3(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
