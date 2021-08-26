package org.telegram.messenger.browser;

import android.content.Context;
import android.net.Uri;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class Browser$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ AlertDialog[] f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Uri f$3;
    public final /* synthetic */ Context f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ Browser$$ExternalSyntheticLambda2(AlertDialog[] alertDialogArr, TLObject tLObject, int i, Uri uri, Context context, boolean z) {
        this.f$0 = alertDialogArr;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = uri;
        this.f$4 = context;
        this.f$5 = z;
    }

    public final void run() {
        Browser.lambda$openUrl$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
