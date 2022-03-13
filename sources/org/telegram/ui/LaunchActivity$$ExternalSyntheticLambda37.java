package org.telegram.ui;

import android.net.Uri;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Uri f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ AlertDialog f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda37(LaunchActivity launchActivity, TLObject tLObject, Uri uri, int i, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = uri;
        this.f$3 = i;
        this.f$4 = alertDialog;
    }

    public final void run() {
        this.f$0.lambda$runImportRequest$25(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
