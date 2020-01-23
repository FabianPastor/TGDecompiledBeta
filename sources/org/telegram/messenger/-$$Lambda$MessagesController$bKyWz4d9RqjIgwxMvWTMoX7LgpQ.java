package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ INSTANCE = new -$$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ();

    private /* synthetic */ -$$Lambda$MessagesController$bKyWz4d9RqjIgwxMvWTMoX7LgpQ() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$158(tLObject, tL_error);
    }
}
