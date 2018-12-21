package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ProfileActivity.CLASSNAME;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ProfileActivity$3$$Lambda$3 */
final /* synthetic */ class ProfileActivity$3$$Lambda$3 implements RequestDelegate {
    private final CLASSNAME arg$1;
    private final AlertDialog[] arg$2;

    ProfileActivity$3$$Lambda$3(CLASSNAME CLASSNAME, AlertDialog[] alertDialogArr) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = alertDialogArr;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$onItemClick$4$ProfileActivity$3(this.arg$2, tLObject, tL_error);
    }
}
