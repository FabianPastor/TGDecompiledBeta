package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeBioActivity$T2lBd_q-6K_Uw6G0hNAJdgSSX7I implements Runnable {
    private final /* synthetic */ ChangeBioActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TL_account_updateProfile f$3;

    public /* synthetic */ -$$Lambda$ChangeBioActivity$T2lBd_q-6K_Uw6G0hNAJdgSSX7I(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TL_error tL_error, TL_account_updateProfile tL_account_updateProfile) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_account_updateProfile;
    }

    public final void run() {
        this.f$0.lambda$null$3$ChangeBioActivity(this.f$1, this.f$2, this.f$3);
    }
}
