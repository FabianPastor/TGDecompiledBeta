package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_updateProfile;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$UserFull f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$TL_account_updateProfile f$4;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda5(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TLRPC$UserFull tLRPC$UserFull, String str, TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$UserFull;
        this.f$3 = str;
        this.f$4 = tLRPC$TL_account_updateProfile;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$saveName$4(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
