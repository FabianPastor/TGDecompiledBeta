package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeUsernameActivity$v8ZSRwHtLRMnSL9d8TsxyGq5Q58 implements RequestDelegate {
    private final /* synthetic */ ChangeUsernameActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_account_updateUsername f$2;

    public /* synthetic */ -$$Lambda$ChangeUsernameActivity$v8ZSRwHtLRMnSL9d8TsxyGq5Q58(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TL_account_updateUsername tL_account_updateUsername) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_account_updateUsername;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$saveName$7$ChangeUsernameActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
