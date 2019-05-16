package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$A-WQ0vAsaYkwbum0RRlzdSDsvGo implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$A-WQ0vAsaYkwbum0RRlzdSDsvGo INSTANCE = new -$$Lambda$MessagesController$A-WQ0vAsaYkwbum0RRlzdSDsvGo();

    private /* synthetic */ -$$Lambda$MessagesController$A-WQ0vAsaYkwbum0RRlzdSDsvGo() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$25(tLObject, tL_error);
    }
}
