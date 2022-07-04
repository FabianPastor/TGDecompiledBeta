package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda10 implements RequestDelegate {
    public static final /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda10 INSTANCE = new GroupCallActivity$6$$ExternalSyntheticLambda10();

    private /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda10() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        GroupCallActivity.AnonymousClass6.lambda$onItemClick$8(tLObject, tL_error);
    }
}
