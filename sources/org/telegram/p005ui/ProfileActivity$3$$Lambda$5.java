package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ProfileActivity.CLASSNAME;
import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ProfileActivity$3$$Lambda$5 */
final /* synthetic */ class ProfileActivity$3$$Lambda$5 implements Runnable {
    private final CLASSNAME arg$1;
    private final AlertDialog[] arg$2;
    private final TLObject arg$3;

    ProfileActivity$3$$Lambda$5(CLASSNAME CLASSNAME, AlertDialog[] alertDialogArr, TLObject tLObject) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = alertDialogArr;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$ProfileActivity$3(this.arg$2, this.arg$3);
    }
}
