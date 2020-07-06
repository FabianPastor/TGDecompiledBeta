package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$H5aibf1NSg8ycDx9_rKfY4vg1MQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$H5aibf1NSg8ycDx9_rKfY4vg1MQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$H5aibf1NSg8ycDx9_rKfY4vg1MQ INSTANCE = new $$Lambda$MessagesController$H5aibf1NSg8ycDx9_rKfY4vg1MQ();

    private /* synthetic */ $$Lambda$MessagesController$H5aibf1NSg8ycDx9_rKfY4vg1MQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$176(tLObject, tLRPC$TL_error);
    }
}
