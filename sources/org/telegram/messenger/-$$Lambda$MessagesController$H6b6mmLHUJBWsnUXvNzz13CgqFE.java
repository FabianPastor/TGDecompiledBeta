package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$H6b6mmLHUJBWsnUXvNzz13CgqFE implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$H6b6mmLHUJBWsnUXvNzz13CgqFE INSTANCE = new -$$Lambda$MessagesController$H6b6mmLHUJBWsnUXvNzz13CgqFE();

    private /* synthetic */ -$$Lambda$MessagesController$H6b6mmLHUJBWsnUXvNzz13CgqFE() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$242(tLObject, tL_error);
    }
}
