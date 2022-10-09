package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public static final /* synthetic */ PassportActivity$$ExternalSyntheticLambda68 INSTANCE = new PassportActivity$$ExternalSyntheticLambda68();

    private /* synthetic */ PassportActivity$$ExternalSyntheticLambda68() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PassportActivity.lambda$new$1(tLObject, tLRPC$TL_error);
    }
}
