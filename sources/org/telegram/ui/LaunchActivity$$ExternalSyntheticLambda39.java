package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda39 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Theme.ThemeInfo f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda39(LaunchActivity launchActivity, TLObject tLObject, Theme.ThemeInfo themeInfo) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = themeInfo;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$73(this.f$1, this.f$2);
    }
}
