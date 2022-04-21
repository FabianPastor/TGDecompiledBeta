package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLRPC.TL_account_updateUsername f$3;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda5(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TLRPC.TL_error tL_error, TLRPC.TL_account_updateUsername tL_account_updateUsername) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_account_updateUsername;
    }

    public final void run() {
        this.f$0.m1577lambda$saveName$6$orgtelegramuiChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
    }
}
