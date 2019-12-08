package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$WNefRv74qynzPsn_w__r0A_GjNo implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$WNefRv74qynzPsn_w__r0A_GjNo INSTANCE = new -$$Lambda$MessagesController$WNefRv74qynzPsn_w__r0A_GjNo();

    private /* synthetic */ -$$Lambda$MessagesController$WNefRv74qynzPsn_w__r0A_GjNo() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$completeReadTask$162(tLObject, tL_error);
    }
}
