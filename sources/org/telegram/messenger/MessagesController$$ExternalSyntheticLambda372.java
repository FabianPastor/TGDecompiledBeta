package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda372 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda372 INSTANCE = new MessagesController$$ExternalSyntheticLambda372();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda372() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$99(tLObject, tLRPC$TL_error);
    }
}
