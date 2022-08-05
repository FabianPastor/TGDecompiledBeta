package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ String[] f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ Theme$$ExternalSyntheticLambda8(String[] strArr, Theme.ThemeInfo themeInfo, boolean z, boolean z2, Runnable runnable) {
        this.f$0 = strArr;
        this.f$1 = themeInfo;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = runnable;
    }

    public final void run() {
        Theme.lambda$applyThemeInBackground$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
