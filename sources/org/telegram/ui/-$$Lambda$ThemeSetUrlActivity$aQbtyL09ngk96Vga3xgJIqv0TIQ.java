package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeSetUrlActivity$aQbtyL09ngk96Vga3xgJIqv0TIQ implements RequestDelegate {
    private final /* synthetic */ ThemeSetUrlActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$ThemeSetUrlActivity$aQbtyL09ngk96Vga3xgJIqv0TIQ(ThemeSetUrlActivity themeSetUrlActivity, String str) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$7$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
    }
}
