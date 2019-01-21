package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

final /* synthetic */ class ProfileActivity$3$$Lambda$5 implements Runnable {
    private final AnonymousClass3 arg$1;
    private final AlertDialog[] arg$2;
    private final TLObject arg$3;

    ProfileActivity$3$$Lambda$5(AnonymousClass3 anonymousClass3, AlertDialog[] alertDialogArr, TLObject tLObject) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = alertDialogArr;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$ProfileActivity$3(this.arg$2, this.arg$3);
    }
}
