package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$Rvi4kv27E1B4N6_m-qmj5cSuMIA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$Rvi4kv27E1B4N6_mqmj5cSuMIA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$Rvi4kv27E1B4N6_mqmj5cSuMIA INSTANCE = new $$Lambda$MessagesController$Rvi4kv27E1B4N6_mqmj5cSuMIA();

    private /* synthetic */ $$Lambda$MessagesController$Rvi4kv27E1B4N6_mqmj5cSuMIA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$46(tLObject, tLRPC$TL_error);
    }
}
