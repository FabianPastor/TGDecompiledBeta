package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$1qQoRp50btGnJtbibT-LgfHqlKQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$1qQoRp50btGnJtbibTLgfHqlKQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$1qQoRp50btGnJtbibTLgfHqlKQ INSTANCE = new $$Lambda$MessagesController$1qQoRp50btGnJtbibTLgfHqlKQ();

    private /* synthetic */ $$Lambda$MessagesController$1qQoRp50btGnJtbibTLgfHqlKQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$hidePromoDialog$94(tLObject, tLRPC$TL_error);
    }
}
