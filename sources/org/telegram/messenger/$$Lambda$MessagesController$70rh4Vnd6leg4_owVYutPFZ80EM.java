package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$70rh4Vnd6leg4_owVYutPFZ80EM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$70rh4Vnd6leg4_owVYutPFZ80EM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$70rh4Vnd6leg4_owVYutPFZ80EM INSTANCE = new $$Lambda$MessagesController$70rh4Vnd6leg4_owVYutPFZ80EM();

    private /* synthetic */ $$Lambda$MessagesController$70rh4Vnd6leg4_owVYutPFZ80EM() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$installTheme$81(tLObject, tLRPC$TL_error);
    }
}
