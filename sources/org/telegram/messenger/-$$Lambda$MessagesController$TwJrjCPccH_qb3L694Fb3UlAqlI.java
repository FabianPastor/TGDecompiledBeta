package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$TwJrjCPccH_qb3L694Fb3UlAqlI implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$TwJrjCPccH_qb3L694Fb3UlAqlI INSTANCE = new -$$Lambda$MessagesController$TwJrjCPccH_qb3L694Fb3UlAqlI();

    private /* synthetic */ -$$Lambda$MessagesController$TwJrjCPccH_qb3L694Fb3UlAqlI() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$240(tLObject, tL_error);
    }
}
