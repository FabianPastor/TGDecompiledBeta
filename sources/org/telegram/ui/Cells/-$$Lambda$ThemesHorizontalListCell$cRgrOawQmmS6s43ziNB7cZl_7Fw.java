package org.telegram.ui.Cells;

import java.io.File;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemesHorizontalListCell$cRgrOawQmmS6s43ziNB7cZl_7Fw implements Runnable {
    private final /* synthetic */ ThemesHorizontalListCell f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ ThemeInfo f$2;

    public /* synthetic */ -$$Lambda$ThemesHorizontalListCell$cRgrOawQmmS6s43ziNB7cZl_7Fw(ThemesHorizontalListCell themesHorizontalListCell, File file, ThemeInfo themeInfo) {
        this.f$0 = themesHorizontalListCell;
        this.f$1 = file;
        this.f$2 = themeInfo;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$3$ThemesHorizontalListCell(this.f$1, this.f$2);
    }
}
