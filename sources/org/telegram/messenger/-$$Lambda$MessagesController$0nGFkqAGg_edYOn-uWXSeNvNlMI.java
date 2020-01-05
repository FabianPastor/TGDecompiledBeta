package org.telegram.messenger;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$0nGFkqAGg_edYOn-uWXSeNvNlMI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ File f$2;
    private final /* synthetic */ ThemeAccent f$3;
    private final /* synthetic */ ThemeInfo f$4;

    public /* synthetic */ -$$Lambda$MessagesController$0nGFkqAGg_edYOn-uWXSeNvNlMI(MessagesController messagesController, String str, File file, ThemeAccent themeAccent, ThemeInfo themeInfo) {
        this.f$0 = messagesController;
        this.f$1 = str;
        this.f$2 = file;
        this.f$3 = themeAccent;
        this.f$4 = themeInfo;
    }

    public final void run() {
        this.f$0.lambda$saveThemeToServer$74$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
