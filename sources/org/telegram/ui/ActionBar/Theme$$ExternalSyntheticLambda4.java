package org.telegram.ui.ActionBar;

import java.io.File;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ Utilities.Callback f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ String[] f$3;

    public /* synthetic */ Theme$$ExternalSyntheticLambda4(Utilities.Callback callback, File file, String str, String[] strArr) {
        this.f$0 = callback;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = strArr;
    }

    public final void run() {
        this.f$0.run(Theme.getThemeFileValues(this.f$1, this.f$2, this.f$3));
    }
}
