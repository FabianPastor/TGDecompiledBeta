package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updateTheme;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeSetUrlActivity$OFM5DeupJh-qZ2ccB6uZLjCNlMI implements Runnable {
    private final /* synthetic */ ThemeSetUrlActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_account_updateTheme f$2;

    public /* synthetic */ -$$Lambda$ThemeSetUrlActivity$OFM5DeupJh-qZ2ccB6uZLjCNlMI(ThemeSetUrlActivity themeSetUrlActivity, TL_error tL_error, TL_account_updateTheme tL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_account_updateTheme;
    }

    public final void run() {
        this.f$0.lambda$null$11$ThemeSetUrlActivity(this.f$1, this.f$2);
    }
}
