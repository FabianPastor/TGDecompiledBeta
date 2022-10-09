package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda9 INSTANCE = new GroupCallActivity$6$$ExternalSyntheticLambda9();

    private /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda9() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        GroupCallActivity.AnonymousClass6.lambda$onItemClick$8(tLObject, tLRPC$TL_error);
    }
}
