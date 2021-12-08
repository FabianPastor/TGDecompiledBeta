package org.telegram.ui.ActionBar;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ TLObject f$0;
    public final /* synthetic */ Theme.ThemeAccent f$1;
    public final /* synthetic */ Theme.ThemeInfo f$2;
    public final /* synthetic */ TLRPC.TL_theme f$3;

    public /* synthetic */ Theme$$ExternalSyntheticLambda6(TLObject tLObject, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, TLRPC.TL_theme tL_theme) {
        this.f$0 = tLObject;
        this.f$1 = themeAccent;
        this.f$2 = themeInfo;
        this.f$3 = tL_theme;
    }

    public final void run() {
        Theme.lambda$checkCurrentRemoteTheme$3(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
