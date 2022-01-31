package org.telegram.messenger;

import java.io.File;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda96 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ File f$2;
    public final /* synthetic */ Theme.ThemeAccent f$3;
    public final /* synthetic */ Theme.ThemeInfo f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda96(MessagesController messagesController, String str, File file, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        this.f$0 = messagesController;
        this.f$1 = str;
        this.f$2 = file;
        this.f$3 = themeAccent;
        this.f$4 = themeInfo;
    }

    public final void run() {
        this.f$0.lambda$saveThemeToServer$94(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
