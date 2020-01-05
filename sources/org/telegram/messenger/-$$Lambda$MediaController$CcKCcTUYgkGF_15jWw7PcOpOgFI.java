package org.telegram.messenger;

import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$CcKCcTUYgkGF_15jWw7PcOpOgFI implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ File f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ boolean[] f$3;
    private final /* synthetic */ AlertDialog f$4;
    private final /* synthetic */ String f$5;

    public /* synthetic */ -$$Lambda$MediaController$CcKCcTUYgkGF_15jWw7PcOpOgFI(int i, File file, String str, boolean[] zArr, AlertDialog alertDialog, String str2) {
        this.f$0 = i;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = zArr;
        this.f$4 = alertDialog;
        this.f$5 = str2;
    }

    public final void run() {
        MediaController.lambda$saveFile$27(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
