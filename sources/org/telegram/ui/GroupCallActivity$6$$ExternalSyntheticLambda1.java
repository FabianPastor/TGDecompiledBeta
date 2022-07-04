package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda1 implements JoinCallAlert.JoinCallAlertDelegate {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6 f$0;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda1(GroupCallActivity.AnonymousClass6 r1) {
        this.f$0 = r1;
    }

    public final void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2) {
        this.f$0.m3561lambda$onItemClick$9$orgtelegramuiGroupCallActivity$6(inputPeer, z, z2);
    }
}
