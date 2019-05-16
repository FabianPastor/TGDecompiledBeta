package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StickersAlert$70AzTfODxLbeSRDlB8gJ7WKPlnE implements RequestDelegate {
    private final /* synthetic */ StickersAlert f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ TL_messages_getAttachedStickers f$2;
    private final /* synthetic */ RequestDelegate f$3;

    public /* synthetic */ -$$Lambda$StickersAlert$70AzTfODxLbeSRDlB8gJ7WKPlnE(StickersAlert stickersAlert, Object obj, TL_messages_getAttachedStickers tL_messages_getAttachedStickers, RequestDelegate requestDelegate) {
        this.f$0 = stickersAlert;
        this.f$1 = obj;
        this.f$2 = tL_messages_getAttachedStickers;
        this.f$3 = requestDelegate;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$new$2$StickersAlert(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
