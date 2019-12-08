package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$IXjG5rWXs9Rasb_rX9lsPENNKyw implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MediaDataController$IXjG5rWXs9Rasb_rX9lsPENNKyw INSTANCE = new -$$Lambda$MediaDataController$IXjG5rWXs9Rasb_rX9lsPENNKyw();

    private /* synthetic */ -$$Lambda$MediaDataController$IXjG5rWXs9Rasb_rX9lsPENNKyw() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$28(tLObject, tL_error);
    }
}
