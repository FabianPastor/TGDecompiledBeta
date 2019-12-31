package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$svh6g1eAA1ckXbR4FzwxJEf4RsY implements RequestDelegate {
    private final /* synthetic */ TL_theme f$0;
    private final /* synthetic */ ThemeAccent f$1;
    private final /* synthetic */ ThemeInfo f$2;

    public /* synthetic */ -$$Lambda$Theme$svh6g1eAA1ckXbR4FzwxJEf4RsY(TL_theme tL_theme, ThemeAccent themeAccent, ThemeInfo themeInfo) {
        this.f$0 = tL_theme;
        this.f$1 = themeAccent;
        this.f$2 = themeInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$xUyl4Wlvar_OIslQJNxBDYIy9o8(tLObject, this.f$0, this.f$1, this.f$2));
    }
}
