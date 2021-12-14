package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC$User f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda36(GroupCallActivity groupCallActivity, long j, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User) {
        this.f$0 = groupCallActivity;
        this.f$1 = j;
        this.f$2 = alertDialogArr;
        this.f$3 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$inviteUserToCall$44(this.f$1, this.f$2, this.f$3);
    }
}
