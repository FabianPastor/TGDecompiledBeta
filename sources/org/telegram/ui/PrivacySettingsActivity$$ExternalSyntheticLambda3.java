package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_account_setAccountTTL f$2;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda3(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TLRPC.TL_account_setAccountTTL tL_account_setAccountTTL) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_account_setAccountTTL;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3670lambda$createView$3$orgtelegramuiPrivacySettingsActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
