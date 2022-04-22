package org.telegram.messenger;

import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class MediaController$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ File f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ AlertDialog f$3;
    public final /* synthetic */ boolean[] f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ Runnable f$6;
    public final /* synthetic */ boolean[] f$7;

    public /* synthetic */ MediaController$$ExternalSyntheticLambda5(int i, File file, String str, AlertDialog alertDialog, boolean[] zArr, String str2, Runnable runnable, boolean[] zArr2) {
        this.f$0 = i;
        this.f$1 = file;
        this.f$2 = str;
        this.f$3 = alertDialog;
        this.f$4 = zArr;
        this.f$5 = str2;
        this.f$6 = runnable;
        this.f$7 = zArr2;
    }

    public final void run() {
        MediaController.lambda$saveFile$38(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
