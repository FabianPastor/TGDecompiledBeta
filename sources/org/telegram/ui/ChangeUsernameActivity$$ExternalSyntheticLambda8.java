package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_updateUsername;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_account_updateUsername f$2;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda8(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TLRPC$TL_account_updateUsername tLRPC$TL_account_updateUsername) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_account_updateUsername;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$saveName$7(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
