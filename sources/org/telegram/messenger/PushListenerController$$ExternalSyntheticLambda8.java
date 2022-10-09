package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class PushListenerController$$ExternalSyntheticLambda8 implements RequestDelegate {
    public static final /* synthetic */ PushListenerController$$ExternalSyntheticLambda8 INSTANCE = new PushListenerController$$ExternalSyntheticLambda8();

    private /* synthetic */ PushListenerController$$ExternalSyntheticLambda8() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PushListenerController.lambda$sendRegistrationToServer$1(tLObject, tLRPC$TL_error);
    }
}
