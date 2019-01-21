package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class ChangeBioActivity$$Lambda$2 implements RequestDelegate {
    private final ChangeBioActivity arg$1;
    private final AlertDialog arg$2;
    private final TL_userFull arg$3;
    private final String arg$4;
    private final TL_account_updateProfile arg$5;

    ChangeBioActivity$$Lambda$2(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TL_userFull tL_userFull, String str, TL_account_updateProfile tL_account_updateProfile) {
        this.arg$1 = changeBioActivity;
        this.arg$2 = alertDialog;
        this.arg$3 = tL_userFull;
        this.arg$4 = str;
        this.arg$5 = tL_account_updateProfile;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$saveName$4$ChangeBioActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
