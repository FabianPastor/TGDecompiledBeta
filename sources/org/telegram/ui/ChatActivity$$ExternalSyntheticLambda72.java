package org.telegram.ui;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda72 implements Runnable {
    public final /* synthetic */ Theme.ThemeAccent f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda72(Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, boolean z) {
        this.f$0 = themeAccent;
        this.f$1 = themeInfo;
        this.f$2 = z;
    }

    public final void run() {
        ChatActivity.lambda$didReceivedNotification$89(this.f$0, this.f$1, this.f$2);
    }
}
