package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda183 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ Theme.ThemeAccent f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda183(MessagesController messagesController, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = themeInfo;
        this.f$2 = themeAccent;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$26(this.f$1, this.f$2);
    }
}
