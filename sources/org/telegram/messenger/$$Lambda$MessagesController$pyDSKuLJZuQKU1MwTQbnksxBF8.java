package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$p-yDSKuLJZuQKU1MwTQbnksxBF8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$pyDSKuLJZuQKU1MwTQbnksxBF8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$pyDSKuLJZuQKU1MwTQbnksxBF8 INSTANCE = new $$Lambda$MessagesController$pyDSKuLJZuQKU1MwTQbnksxBF8();

    private /* synthetic */ $$Lambda$MessagesController$pyDSKuLJZuQKU1MwTQbnksxBF8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$44(tLObject, tLRPC$TL_error);
    }
}
