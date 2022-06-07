package org.telegram.ui;

import java.io.File;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda60 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ File f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda60(LaunchActivity launchActivity, Theme.ThemeInfo themeInfo, File file) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
        this.f$2 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$92(this.f$1, this.f$2);
    }
}
