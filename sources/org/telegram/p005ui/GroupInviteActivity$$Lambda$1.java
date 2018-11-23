package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.GroupInviteActivity$$Lambda$1 */
final /* synthetic */ class GroupInviteActivity$$Lambda$1 implements RequestDelegate {
    private final GroupInviteActivity arg$1;
    private final boolean arg$2;

    GroupInviteActivity$$Lambda$1(GroupInviteActivity groupInviteActivity, boolean z) {
        this.arg$1 = groupInviteActivity;
        this.arg$2 = z;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$generateLink$3$GroupInviteActivity(this.arg$2, tLObject, tL_error);
    }
}
