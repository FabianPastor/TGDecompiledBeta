package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_setAccountTTL;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ PrivacySettingsActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_account_setAccountTTL f$2;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda14(PrivacySettingsActivity privacySettingsActivity, AlertDialog alertDialog, TLRPC$TL_account_setAccountTTL tLRPC$TL_account_setAccountTTL) {
        this.f$0 = privacySettingsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_account_setAccountTTL;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$3(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
