package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6 f$0;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda9(GroupCallActivity.AnonymousClass6 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3556lambda$onItemClick$0$orgtelegramuiGroupCallActivity$6(tLObject, tL_error);
    }
}
