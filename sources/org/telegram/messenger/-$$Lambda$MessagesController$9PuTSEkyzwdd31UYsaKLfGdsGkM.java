package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$9PuTSEkyzwdd31UYsaKLfGdsGkM implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$9PuTSEkyzwdd31UYsaKLfGdsGkM INSTANCE = new -$$Lambda$MessagesController$9PuTSEkyzwdd31UYsaKLfGdsGkM();

    private /* synthetic */ -$$Lambda$MessagesController$9PuTSEkyzwdd31UYsaKLfGdsGkM() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$processUpdates$236(tLObject, tL_error);
    }
}
