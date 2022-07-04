package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChangeNameActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public static final /* synthetic */ ChangeNameActivity$$ExternalSyntheticLambda4 INSTANCE = new ChangeNameActivity$$ExternalSyntheticLambda4();

    private /* synthetic */ ChangeNameActivity$$ExternalSyntheticLambda4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChangeNameActivity.lambda$saveName$3(tLObject, tLRPC$TL_error);
    }
}
