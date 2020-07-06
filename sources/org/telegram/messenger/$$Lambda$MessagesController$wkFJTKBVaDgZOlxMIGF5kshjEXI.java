package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$wkFJTKBVaDgZOlxMIGF5kshjEXI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$wkFJTKBVaDgZOlxMIGF5kshjEXI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$wkFJTKBVaDgZOlxMIGF5kshjEXI INSTANCE = new $$Lambda$MessagesController$wkFJTKBVaDgZOlxMIGF5kshjEXI();

    private /* synthetic */ $$Lambda$MessagesController$wkFJTKBVaDgZOlxMIGF5kshjEXI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$267(tLObject, tLRPC$TL_error);
    }
}
