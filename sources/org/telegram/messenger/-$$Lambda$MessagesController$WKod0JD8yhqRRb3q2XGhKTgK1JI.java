package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI INSTANCE = new -$$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI();

    private /* synthetic */ -$$Lambda$MessagesController$WKod0JD8yhqRRb3q2XGhKTgK1JI() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$165(tLObject, tL_error);
    }
}
