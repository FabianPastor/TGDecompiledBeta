package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.UserFull f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.User f$4;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda4(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, TLRPC.UserFull userFull, String str, TLRPC.User user) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = userFull;
        this.f$3 = str;
        this.f$4 = user;
    }

    public final void run() {
        this.f$0.m1542lambda$saveName$2$orgtelegramuiChangeBioActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
