package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ Theme.ThemeAccent f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ TLRPC$TL_theme f$2;

    public /* synthetic */ Theme$$ExternalSyntheticLambda10(Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, TLRPC$TL_theme tLRPC$TL_theme) {
        this.f$0 = themeAccent;
        this.f$1 = themeInfo;
        this.f$2 = tLRPC$TL_theme;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda4(tLObject, this.f$0, this.f$1, this.f$2));
    }
}
