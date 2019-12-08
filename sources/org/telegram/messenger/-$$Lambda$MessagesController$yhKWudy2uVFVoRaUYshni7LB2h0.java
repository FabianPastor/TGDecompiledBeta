package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$yhKWudy2uVFVoRaUYshni7LB2h0 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$yhKWudy2uVFVoRaUYshni7LB2h0 INSTANCE = new -$$Lambda$MessagesController$yhKWudy2uVFVoRaUYshni7LB2h0();

    private /* synthetic */ -$$Lambda$MessagesController$yhKWudy2uVFVoRaUYshni7LB2h0() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$151(tLObject, tL_error);
    }
}
