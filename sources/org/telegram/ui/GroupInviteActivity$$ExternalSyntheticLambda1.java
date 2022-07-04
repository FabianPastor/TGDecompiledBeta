package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupInviteActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ GroupInviteActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ GroupInviteActivity$$ExternalSyntheticLambda1(GroupInviteActivity groupInviteActivity, TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        this.f$0 = groupInviteActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.m3592lambda$generateLink$2$orgtelegramuiGroupInviteActivity(this.f$1, this.f$2, this.f$3);
    }
}
