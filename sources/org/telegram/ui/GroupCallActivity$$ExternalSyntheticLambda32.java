package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC.User f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda32(GroupCallActivity groupCallActivity, long j, AlertDialog[] alertDialogArr, TLRPC.User user) {
        this.f$0 = groupCallActivity;
        this.f$1 = j;
        this.f$2 = alertDialogArr;
        this.f$3 = user;
    }

    public final void run() {
        this.f$0.m3506lambda$inviteUserToCall$44$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3);
    }
}
