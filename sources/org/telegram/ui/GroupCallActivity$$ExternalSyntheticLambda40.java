package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda40 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda40(GroupCallActivity groupCallActivity) {
        this.f$0 = groupCallActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2990lambda$toggleAdminSpeak$60$orgtelegramuiGroupCallActivity(tLObject, tL_error);
    }
}
