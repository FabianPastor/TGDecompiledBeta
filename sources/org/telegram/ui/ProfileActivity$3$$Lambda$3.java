package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

final /* synthetic */ class ProfileActivity$3$$Lambda$3 implements RequestDelegate {
    private final AnonymousClass3 arg$1;
    private final AlertDialog[] arg$2;

    ProfileActivity$3$$Lambda$3(AnonymousClass3 anonymousClass3, AlertDialog[] alertDialogArr) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = alertDialogArr;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onItemClick$4$ProfileActivity$3(this.arg$2, tLObject, tL_error);
    }
}
