package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4 INSTANCE = new -$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4();

    private /* synthetic */ -$$Lambda$MessagesController$41Nrry903aXeWspe_mkLTT84RF4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$254(tLObject, tL_error);
    }
}
