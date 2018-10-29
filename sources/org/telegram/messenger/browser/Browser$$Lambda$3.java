package org.telegram.messenger.browser;

import android.content.Context;
import android.net.Uri;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class Browser$$Lambda$3 implements Runnable {
    private final AlertDialog[] arg$1;
    private final TLObject arg$2;
    private final int arg$3;
    private final Uri arg$4;
    private final Context arg$5;
    private final boolean arg$6;

    Browser$$Lambda$3(AlertDialog[] alertDialogArr, TLObject tLObject, int i, Uri uri, Context context, boolean z) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = tLObject;
        this.arg$3 = i;
        this.arg$4 = uri;
        this.arg$5 = context;
        this.arg$6 = z;
    }

    public void run() {
        Browser.lambda$null$0$Browser(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
