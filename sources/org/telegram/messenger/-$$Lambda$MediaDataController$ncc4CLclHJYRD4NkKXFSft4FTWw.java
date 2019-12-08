package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ncc4CLclHJYRD4NkKXFSft4FTWw implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MediaDataController$ncc4CLclHJYRD4NkKXFSft4FTWw INSTANCE = new -$$Lambda$MediaDataController$ncc4CLclHJYRD4NkKXFSft4FTWw();

    private /* synthetic */ -$$Lambda$MediaDataController$ncc4CLclHJYRD4NkKXFSft4FTWw() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$28(tLObject, tL_error);
    }
}
