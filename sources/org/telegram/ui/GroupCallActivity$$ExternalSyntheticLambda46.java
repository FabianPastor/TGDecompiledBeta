package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$TL_error f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$5;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda46(GroupCallActivity groupCallActivity, AlertDialog[] alertDialogArr, boolean z, TLRPC$TL_error tLRPC$TL_error, long j, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = z;
        this.f$3 = tLRPC$TL_error;
        this.f$4 = j;
        this.f$5 = tLRPC$TL_phone_inviteToGroupCall;
    }

    public final void run() {
        this.f$0.lambda$inviteUserToCall$45(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
