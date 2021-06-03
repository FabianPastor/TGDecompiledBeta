package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$UhhJlWdtli4SumjSksiOpvdMMSs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$UhhJlWdtli4SumjSksiOpvdMMSs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$UhhJlWdtli4SumjSksiOpvdMMSs INSTANCE = new $$Lambda$MessagesController$UhhJlWdtli4SumjSksiOpvdMMSs();

    private /* synthetic */ $$Lambda$MessagesController$UhhJlWdtli4SumjSksiOpvdMMSs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unregistedPush$233(tLObject, tLRPC$TL_error);
    }
}
