package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupInviteActivity$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ GroupInviteActivity f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ GroupInviteActivity$$ExternalSyntheticLambda2(GroupInviteActivity groupInviteActivity, boolean z) {
        this.f$0 = groupInviteActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$generateLink$3(this.f$1, tLObject, tLRPC$TL_error);
    }
}