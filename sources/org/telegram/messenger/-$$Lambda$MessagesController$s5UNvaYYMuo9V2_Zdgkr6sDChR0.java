package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$s5UNvaYYMuo9V2_Zdgkr6sDChR0 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$s5UNvaYYMuo9V2_Zdgkr6sDChR0 INSTANCE = new -$$Lambda$MessagesController$s5UNvaYYMuo9V2_Zdgkr6sDChR0();

    private /* synthetic */ -$$Lambda$MessagesController$s5UNvaYYMuo9V2_Zdgkr6sDChR0() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$146(tLObject, tL_error);
    }
}
