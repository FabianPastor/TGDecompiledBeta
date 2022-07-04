package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda45 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda45(GroupCallActivity groupCallActivity) {
        this.f$0 = groupCallActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3544lambda$toggleAdminSpeak$62$orgtelegramuiGroupCallActivity(tLObject, tL_error);
    }
}
