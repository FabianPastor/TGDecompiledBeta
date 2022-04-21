package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ExternalActionActivity$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ExternalActionActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;

    public /* synthetic */ ExternalActionActivity$$ExternalSyntheticLambda7(ExternalActionActivity externalActionActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error) {
        this.f$0 = externalActionActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
    }

    public final void run() {
        this.f$0.m2140lambda$handleIntent$9$orgtelegramuiExternalActionActivity(this.f$1, this.f$2);
    }
}
