package org.telegram.ui;

import android.net.Uri;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ Uri f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ AlertDialog f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda68(LaunchActivity launchActivity, Uri uri, int i, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = uri;
        this.f$2 = i;
        this.f$3 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2343lambda$runImportRequest$25$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
