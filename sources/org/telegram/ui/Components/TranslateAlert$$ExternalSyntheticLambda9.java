package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ TranslateAlert$$ExternalSyntheticLambda9 INSTANCE = new TranslateAlert$$ExternalSyntheticLambda9();

    private /* synthetic */ TranslateAlert$$ExternalSyntheticLambda9() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TranslateAlert.lambda$translateText$9(tLObject, tL_error);
    }
}
