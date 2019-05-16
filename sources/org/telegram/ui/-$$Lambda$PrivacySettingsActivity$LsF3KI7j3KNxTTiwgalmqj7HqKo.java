package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacySettingsActivity$LsF3KI7j3KNxTTiwgalmqj7HqKo implements Runnable {
    private final /* synthetic */ PrivacySettingsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_account_setAccountTTL f$3;

    public /* synthetic */ -$$Lambda$PrivacySettingsActivity$LsF3KI7j3KNxTTiwgalmqj7HqKo(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TLObject tLObject, TL_account_setAccountTTL tL_account_setAccountTTL) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_account_setAccountTTL;
    }

    public final void run() {
        this.f$0.lambda$null$4$PrivacySettingsActivity(this.f$1, this.f$2, this.f$3);
    }
}
