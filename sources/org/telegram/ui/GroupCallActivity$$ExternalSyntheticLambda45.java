package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda45 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC.TL_phone_inviteToGroupCall f$5;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda45(GroupCallActivity groupCallActivity, long j, AlertDialog[] alertDialogArr, TLRPC.User user, boolean z, TLRPC.TL_phone_inviteToGroupCall tL_phone_inviteToGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = j;
        this.f$2 = alertDialogArr;
        this.f$3 = user;
        this.f$4 = z;
        this.f$5 = tL_phone_inviteToGroupCall;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2194lambda$inviteUserToCall$46$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
