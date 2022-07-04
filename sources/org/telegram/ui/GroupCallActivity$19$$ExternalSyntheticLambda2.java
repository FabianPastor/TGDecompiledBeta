package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$19$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity.AnonymousClass19 f$0;

    public /* synthetic */ GroupCallActivity$19$$ExternalSyntheticLambda2(GroupCallActivity.AnonymousClass19 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onClick$2(tLObject, tLRPC$TL_error);
    }
}
