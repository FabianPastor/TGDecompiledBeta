package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ ActionIntroActivity f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda12(AlertDialog alertDialog, TLObject tLObject, ActionIntroActivity actionIntroActivity, TLRPC.TL_error tL_error) {
        this.f$0 = alertDialog;
        this.f$1 = tLObject;
        this.f$2 = actionIntroActivity;
        this.f$3 = tL_error;
    }

    public final void run() {
        LaunchActivity.lambda$handleIntent$15(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
