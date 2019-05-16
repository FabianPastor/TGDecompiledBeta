package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeBioActivity$JZsWl0x8uGEwh0dAVWe1jdDZvHs implements RequestDelegate {
    private final /* synthetic */ ChangeBioActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ UserFull f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ TL_account_updateProfile f$4;

    public /* synthetic */ -$$Lambda$ChangeBioActivity$JZsWl0x8uGEwh0dAVWe1jdDZvHs(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, UserFull userFull, String str, TL_account_updateProfile tL_account_updateProfile) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = userFull;
        this.f$3 = str;
        this.f$4 = tL_account_updateProfile;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$saveName$4$ChangeBioActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
