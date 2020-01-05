package org.telegram.ui;

import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$EM2AMKNRTq5VcRHDCT-pM4GeUoA implements Runnable {
    private final /* synthetic */ ThemeAccent f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ boolean f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$EM2AMKNRTq5VcRHDCT-pM4GeUoA(ThemeAccent themeAccent, ThemeInfo themeInfo, boolean z) {
        this.f$0 = themeAccent;
        this.f$1 = themeInfo;
        this.f$2 = z;
    }

    public final void run() {
        ChatActivity.lambda$didReceivedNotification$69(this.f$0, this.f$1, this.f$2);
    }
}
