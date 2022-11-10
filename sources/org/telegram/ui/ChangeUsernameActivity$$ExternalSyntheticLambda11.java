package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChangeUsernameActivity$$ExternalSyntheticLambda11 implements RequestDelegate {
    public static final /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda11 INSTANCE = new ChangeUsernameActivity$$ExternalSyntheticLambda11();

    private /* synthetic */ ChangeUsernameActivity$$ExternalSyntheticLambda11() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChangeUsernameActivity.lambda$sendReorder$2(tLObject, tLRPC$TL_error);
    }
}
