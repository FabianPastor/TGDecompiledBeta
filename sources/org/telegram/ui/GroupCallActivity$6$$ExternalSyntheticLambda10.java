package org.telegram.ui;

import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda10 implements JoinCallAlert.JoinCallAlertDelegate {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6 f$0;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda10(GroupCallActivity.AnonymousClass6 r1) {
        this.f$0 = r1;
    }

    public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
        this.f$0.lambda$onItemClick$9(tLRPC$InputPeer, z, z2);
    }
}
