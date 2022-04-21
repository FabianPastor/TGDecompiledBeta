package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda39 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ TLRPC.TL_phone_inviteToGroupCall f$5;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda39(GroupCallActivity groupCallActivity, AlertDialog[] alertDialogArr, boolean z, TLRPC.TL_error tL_error, long j, TLRPC.TL_phone_inviteToGroupCall tL_phone_inviteToGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = z;
        this.f$3 = tL_error;
        this.f$4 = j;
        this.f$5 = tL_phone_inviteToGroupCall;
    }

    public final void run() {
        this.f$0.m2193lambda$inviteUserToCall$45$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
