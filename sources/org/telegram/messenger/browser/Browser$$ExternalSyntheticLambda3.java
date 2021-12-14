package org.telegram.messenger.browser;

import android.content.Context;
import android.net.Uri;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class Browser$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ AlertDialog[] f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Uri f$2;
    public final /* synthetic */ Context f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ Browser$$ExternalSyntheticLambda3(AlertDialog[] alertDialogArr, int i, Uri uri, Context context, boolean z) {
        this.f$0 = alertDialogArr;
        this.f$1 = i;
        this.f$2 = uri;
        this.f$3 = context;
        this.f$4 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Browser$$ExternalSyntheticLambda2(this.f$0, tLObject, this.f$1, this.f$2, this.f$3, this.f$4));
    }
}
