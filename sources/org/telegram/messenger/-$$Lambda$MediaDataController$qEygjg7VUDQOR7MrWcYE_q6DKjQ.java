package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$qEygjg7VUDQOR7MrWcYE_q6DKjQ implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MediaDataController$qEygjg7VUDQOR7MrWcYE_q6DKjQ INSTANCE = new -$$Lambda$MediaDataController$qEygjg7VUDQOR7MrWcYE_q6DKjQ();

    private /* synthetic */ -$$Lambda$MediaDataController$qEygjg7VUDQOR7MrWcYE_q6DKjQ() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$29(tLObject, tL_error);
    }
}
