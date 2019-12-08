package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$ThemeInfo$6uoKlY90X4JDy2IdFv_Jxyk64R4 implements Runnable {
    private final /* synthetic */ ThemeInfo f$0;
    private final /* synthetic */ File f$1;

    public /* synthetic */ -$$Lambda$Theme$ThemeInfo$6uoKlY90X4JDy2IdFv_Jxyk64R4(ThemeInfo themeInfo, File file) {
        this.f$0 = themeInfo;
        this.f$1 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$0$Theme$ThemeInfo(this.f$1);
    }
}
