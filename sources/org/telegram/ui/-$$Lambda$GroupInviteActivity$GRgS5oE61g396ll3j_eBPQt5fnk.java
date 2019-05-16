package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GroupInviteActivity$GRgS5oE61g396ll3j_eBPQt5fnk implements RequestDelegate {
    private final /* synthetic */ GroupInviteActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$GroupInviteActivity$GRgS5oE61g396ll3j_eBPQt5fnk(GroupInviteActivity groupInviteActivity, boolean z) {
        this.f$0 = groupInviteActivity;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$generateLink$3$GroupInviteActivity(this.f$1, tLObject, tL_error);
    }
}
