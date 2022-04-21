package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SessionsActivity$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ SessionsActivity$$ExternalSyntheticLambda8 INSTANCE = new SessionsActivity$$ExternalSyntheticLambda8();

    private /* synthetic */ SessionsActivity$$ExternalSyntheticLambda8() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        SessionsActivity.lambda$createView$0(tLObject, tL_error);
    }
}
