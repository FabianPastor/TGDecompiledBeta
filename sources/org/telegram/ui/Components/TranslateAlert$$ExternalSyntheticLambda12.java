package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda12 implements RequestDelegate {
    public static final /* synthetic */ TranslateAlert$$ExternalSyntheticLambda12 INSTANCE = new TranslateAlert$$ExternalSyntheticLambda12();

    private /* synthetic */ TranslateAlert$$ExternalSyntheticLambda12() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TranslateAlert.lambda$translateText$13(tLObject, tLRPC$TL_error);
    }
}
