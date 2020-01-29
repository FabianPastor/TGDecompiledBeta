package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7-hVixAo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7hVixAo implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7hVixAo INSTANCE = new $$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7hVixAo();

    private /* synthetic */ $$Lambda$MessagesController$WKM5a9edf5QSZ_85kx_7hVixAo() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$saveTheme$70(tLObject, tL_error);
    }
}
