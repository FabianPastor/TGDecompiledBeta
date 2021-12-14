package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$UserFull f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$User f$4;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda4(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TLRPC$UserFull tLRPC$UserFull, String str, TLRPC$User tLRPC$User) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$UserFull;
        this.f$3 = str;
        this.f$4 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$saveName$2(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
