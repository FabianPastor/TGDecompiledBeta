package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$APiRxvKDu_B4Za4CATjRgQ8eiYM implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$APiRxvKDu_B4Za4CATjRgQ8eiYM INSTANCE = new -$$Lambda$MessagesController$APiRxvKDu_B4Za4CATjRgQ8eiYM();

    private /* synthetic */ -$$Lambda$MessagesController$APiRxvKDu_B4Za4CATjRgQ8eiYM() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$142(tLObject, tL_error);
    }
}
