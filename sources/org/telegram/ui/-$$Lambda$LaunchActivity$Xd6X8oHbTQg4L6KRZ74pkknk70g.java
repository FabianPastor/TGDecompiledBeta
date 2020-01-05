package org.telegram.ui;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$Xd6X8oHbTQg4L6KRZ74pkknk70g implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ File f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$Xd6X8oHbTQg4L6KRZ74pkknk70g(LaunchActivity launchActivity, ThemeInfo themeInfo, File file) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
        this.f$2 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$59$LaunchActivity(this.f$1, this.f$2);
    }
}
