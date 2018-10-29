package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class PrivacySettingsActivity$$Lambda$18 implements Runnable {
    private final PrivacySettingsActivity arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final TL_account_setAccountTTL arg$4;

    PrivacySettingsActivity$$Lambda$18(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TLObject tLObject, TL_account_setAccountTTL tL_account_setAccountTTL) {
        this.arg$1 = privacySettingsActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = tL_account_setAccountTTL;
    }

    public void run() {
        this.arg$1.lambda$null$4$PrivacySettingsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
