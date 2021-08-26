package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda48 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AlertDialog[] f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$5;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda48(GroupCallActivity groupCallActivity, int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User, boolean z, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
        this.f$0 = groupCallActivity;
        this.f$1 = i;
        this.f$2 = alertDialogArr;
        this.f$3 = tLRPC$User;
        this.f$4 = z;
        this.f$5 = tLRPC$TL_phone_inviteToGroupCall;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$inviteUserToCall$46(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
