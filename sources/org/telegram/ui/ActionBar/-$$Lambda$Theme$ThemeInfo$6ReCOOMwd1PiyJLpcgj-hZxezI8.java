package org.telegram.ui.ActionBar;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$ThemeInfo$6ReCOOMwd1PiyJLpcgj-hZxezI8 implements RequestDelegate {
    private final /* synthetic */ ThemeInfo f$0;
    private final /* synthetic */ ThemeInfo f$1;

    public /* synthetic */ -$$Lambda$Theme$ThemeInfo$6ReCOOMwd1PiyJLpcgj-hZxezI8(ThemeInfo themeInfo, ThemeInfo themeInfo2) {
        this.f$0 = themeInfo;
        this.f$1 = themeInfo2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$2$Theme$ThemeInfo(this.f$1, tLObject, tL_error);
    }
}
