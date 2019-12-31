package org.telegram.ui.Cells;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemesHorizontalListCell$p_UmD2l5pY3SvkQSJFDqw5WT8mE implements Runnable {
    private final /* synthetic */ ThemesHorizontalListCell f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ File f$2;

    public /* synthetic */ -$$Lambda$ThemesHorizontalListCell$p_UmD2l5pY3SvkQSJFDqw5WT8mE(ThemesHorizontalListCell themesHorizontalListCell, ThemeInfo themeInfo, File file) {
        this.f$0 = themesHorizontalListCell;
        this.f$1 = themeInfo;
        this.f$2 = file;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$3$ThemesHorizontalListCell(this.f$1, this.f$2);
    }
}
