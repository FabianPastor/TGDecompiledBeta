package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$tEyPfAJSjG5jGc1nCoWl-bBI4Zg implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$tEyPfAJSjG5jGc1nCoWl-bBI4Zg INSTANCE = new -$$Lambda$MessagesController$tEyPfAJSjG5jGc1nCoWl-bBI4Zg();

    private /* synthetic */ -$$Lambda$MessagesController$tEyPfAJSjG5jGc1nCoWl-bBI4Zg() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$reportSpam$27(tLObject, tL_error);
    }
}
