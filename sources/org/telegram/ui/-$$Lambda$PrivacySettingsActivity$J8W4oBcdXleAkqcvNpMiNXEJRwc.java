package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacySettingsActivity$J8W4oBcdXleAkqcvNpMiNXEJRwc implements RequestDelegate {
    private final /* synthetic */ PrivacySettingsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_account_setAccountTTL f$2;

    public /* synthetic */ -$$Lambda$PrivacySettingsActivity$J8W4oBcdXleAkqcvNpMiNXEJRwc(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TL_account_setAccountTTL tL_account_setAccountTTL) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_account_setAccountTTL;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$5$PrivacySettingsActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
