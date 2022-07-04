package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_account_updateProfile;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_error f$2;
    public final /* synthetic */ TLRPC$TL_account_updateProfile f$3;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda3(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_error;
        this.f$3 = tLRPC$TL_account_updateProfile;
    }

    public final void run() {
        this.f$0.lambda$saveName$3(this.f$1, this.f$2, this.f$3);
    }
}
