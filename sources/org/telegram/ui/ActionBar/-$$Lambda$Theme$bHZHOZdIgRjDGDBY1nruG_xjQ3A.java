package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$bHZHOZdIgRjDGDBY1nruG_xjQ3A implements RequestDelegate {
    private final /* synthetic */ ThemeAccent f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ TL_theme f$2;

    public /* synthetic */ -$$Lambda$Theme$bHZHOZdIgRjDGDBY1nruG_xjQ3A(ThemeAccent themeAccent, ThemeInfo themeInfo, TL_theme tL_theme) {
        this.f$0 = themeAccent;
        this.f$1 = themeInfo;
        this.f$2 = tL_theme;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$gQFhTuB7GeUjON8X695_fA-tL-s(tLObject, this.f$0, this.f$1, this.f$2));
    }
}
