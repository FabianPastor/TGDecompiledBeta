package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$lJcvZEJyO8Mz9YX2KH774RfKjcc implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ ThemeInfo f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$lJcvZEJyO8Mz9YX2KH774RfKjcc(LaunchActivity launchActivity, ThemeInfo themeInfo) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$52$LaunchActivity(this.f$1, tLObject, tL_error);
    }
}
