package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateTheme;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeSetUrlActivity$RZNy0C-blX8HMmj88uQdPDrohSI implements RequestDelegate {
    private final /* synthetic */ ThemeSetUrlActivity f$0;
    private final /* synthetic */ TL_account_updateTheme f$1;

    public /* synthetic */ -$$Lambda$ThemeSetUrlActivity$RZNy0C-blX8HMmj88uQdPDrohSI(ThemeSetUrlActivity themeSetUrlActivity, TL_account_updateTheme tL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tL_account_updateTheme;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$saveTheme$12$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
    }
}
