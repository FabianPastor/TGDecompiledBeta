package org.telegram.messenger;

import java.io.File;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class MediaController$$Lambda$16 implements Runnable {
    private final int arg$1;
    private final String arg$2;
    private final File arg$3;
    private final boolean[] arg$4;
    private final AlertDialog arg$5;
    private final String arg$6;

    MediaController$$Lambda$16(int i, String str, File file, boolean[] zArr, AlertDialog alertDialog, String str2) {
        this.arg$1 = i;
        this.arg$2 = str;
        this.arg$3 = file;
        this.arg$4 = zArr;
        this.arg$5 = alertDialog;
        this.arg$6 = str2;
    }

    public void run() {
        MediaController.lambda$saveFile$25$MediaController(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
