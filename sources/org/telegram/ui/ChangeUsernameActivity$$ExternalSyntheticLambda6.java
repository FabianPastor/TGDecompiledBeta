package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ChangeUsernameActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda6(ChangeUsernameActivity changeUsernameActivity, AlertDialog alertDialog, TLRPC.User user) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = alertDialog;
        this.f$2 = user;
    }

    public final void run() {
        this.f$0.m2851lambda$saveName$5$orgtelegramuiChangeUsernameActivity(this.f$1, this.f$2);
    }
}
