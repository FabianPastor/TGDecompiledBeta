package org.telegram.ui.Components;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda1 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda1(JoinGroupAlert joinGroupAlert, boolean z) {
        this.f$0 = joinGroupAlert;
        this.f$1 = z;
    }

    public final boolean run(TLRPC.TL_error tL_error) {
        return this.f$0.m1081lambda$new$3$orgtelegramuiComponentsJoinGroupAlert(this.f$1, tL_error);
    }
}
