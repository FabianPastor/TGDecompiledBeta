package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacyControlActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PrivacyControlActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ PrivacyControlActivity$$ExternalSyntheticLambda4(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = privacyControlActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m3656x644a082a(this.f$1, this.f$2, this.f$3);
    }
}
