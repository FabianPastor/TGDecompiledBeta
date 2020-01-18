package org.telegram.ui;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$-B6V0WWj11RTU3AcYX0QmqhCwL0 implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ File f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$-B6V0WWj11RTU3AcYX0QmqhCwL0(LaunchActivity launchActivity, ThemeInfo themeInfo, File file) {
        this.f$0 = launchActivity;
        this.f$1 = themeInfo;
        this.f$2 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$57$LaunchActivity(this.f$1, this.f$2);
    }
}
