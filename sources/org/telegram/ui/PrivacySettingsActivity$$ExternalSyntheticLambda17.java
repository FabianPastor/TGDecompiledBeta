package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_account_setAccountTTL f$3;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda17(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_account_setAccountTTL tL_account_setAccountTTL) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_account_setAccountTTL;
    }

    public final void run() {
        this.f$0.m3019lambda$createView$2$orgtelegramuiPrivacySettingsActivity(this.f$1, this.f$2, this.f$3);
    }
}
