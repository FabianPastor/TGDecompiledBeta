package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChangeUsernameActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChangeUsernameActivity$3$$ExternalSyntheticLambda6 implements RequestDelegate {
    public static final /* synthetic */ ChangeUsernameActivity$3$$ExternalSyntheticLambda6 INSTANCE = new ChangeUsernameActivity$3$$ExternalSyntheticLambda6();

    private /* synthetic */ ChangeUsernameActivity$3$$ExternalSyntheticLambda6() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChangeUsernameActivity.AnonymousClass3.lambda$onItemClick$4(tLObject, tLRPC$TL_error);
    }
}
