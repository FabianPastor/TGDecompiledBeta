package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$S_1cd5sPm9ypEHESC8wq3f0L09w  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$S_1cd5sPm9ypEHESC8wq3f0L09w implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$S_1cd5sPm9ypEHESC8wq3f0L09w INSTANCE = new $$Lambda$MessagesController$S_1cd5sPm9ypEHESC8wq3f0L09w();

    private /* synthetic */ $$Lambda$MessagesController$S_1cd5sPm9ypEHESC8wq3f0L09w() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$173(tLObject, tLRPC$TL_error);
    }
}
