package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupInviteActivity$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ GroupInviteActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ GroupInviteActivity$$ExternalSyntheticLambda2(GroupInviteActivity groupInviteActivity, boolean z) {
        this.f$0 = groupInviteActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2279lambda$generateLink$3$orgtelegramuiGroupInviteActivity(this.f$1, tLObject, tL_error);
    }
}
