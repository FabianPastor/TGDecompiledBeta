package org.telegram.messenger;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda92 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Theme.ThemeAccent f$3;
    public final /* synthetic */ Theme.ThemeInfo f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda92(MessagesController messagesController, String str, String str2, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        this.f$0 = messagesController;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = themeAccent;
        this.f$4 = themeInfo;
    }

    public final void run() {
        this.f$0.lambda$saveThemeToServer$88(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
