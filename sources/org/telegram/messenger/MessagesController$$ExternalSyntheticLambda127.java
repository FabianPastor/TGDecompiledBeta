package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda127 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Theme.ThemeInfo f$2;
    public final /* synthetic */ Theme.ThemeAccent f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda127(MessagesController messagesController, TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = themeInfo;
        this.f$3 = themeAccent;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$22(this.f$1, this.f$2, this.f$3);
    }
}
