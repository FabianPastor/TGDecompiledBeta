package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$pF9AI0vS7PvPoaowEPmtk2Zehvo implements RequestDelegate {
    private final /* synthetic */ ThemeInfo f$0;

    public /* synthetic */ -$$Lambda$Theme$pF9AI0vS7PvPoaowEPmtk2Zehvo(ThemeInfo themeInfo) {
        this.f$0 = themeInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$NtXRXWIRJIkUk2cyT4wt01HmJEM(tLObject, this.f$0));
    }
}
