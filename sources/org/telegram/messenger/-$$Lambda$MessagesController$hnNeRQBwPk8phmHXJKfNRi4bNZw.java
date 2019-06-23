package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hnNeRQBwPk8phmHXJKfNRi4bNZw implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$hnNeRQBwPk8phmHXJKfNRi4bNZw INSTANCE = new -$$Lambda$MessagesController$hnNeRQBwPk8phmHXJKfNRi4bNZw();

    private /* synthetic */ -$$Lambda$MessagesController$hnNeRQBwPk8phmHXJKfNRi4bNZw() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$26(tLObject, tL_error);
    }
}
