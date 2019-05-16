package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeUsernameActivity$dxm9wNh1IKlvP_EniBN7iVOIWGY implements Runnable {
    private final /* synthetic */ ChangeUsernameActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TL_account_updateUsername f$3;

    public /* synthetic */ -$$Lambda$ChangeUsernameActivity$dxm9wNh1IKlvP_EniBN7iVOIWGY(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TL_error tL_error, TL_account_updateUsername tL_account_updateUsername) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_account_updateUsername;
    }

    public final void run() {
        this.f$0.lambda$null$6$ChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
    }
}
