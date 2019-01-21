package org.telegram.messenger.browser;

import android.content.Context;
import android.net.Uri;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class Browser$$Lambda$0 implements RequestDelegate {
    private final AlertDialog[] arg$1;
    private final int arg$2;
    private final Uri arg$3;
    private final Context arg$4;
    private final boolean arg$5;

    Browser$$Lambda$0(AlertDialog[] alertDialogArr, int i, Uri uri, Context context, boolean z) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = i;
        this.arg$3 = uri;
        this.arg$4 = context;
        this.arg$5 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Browser$$Lambda$3(this.arg$1, tLObject, this.arg$2, this.arg$3, this.arg$4, this.arg$5));
    }
}
