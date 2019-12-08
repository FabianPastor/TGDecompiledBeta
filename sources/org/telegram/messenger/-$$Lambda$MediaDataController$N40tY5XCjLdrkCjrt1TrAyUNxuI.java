package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_faveSticker;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$N40tY5XCjLdrkCjrt1TrAyUNxuI implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ Object f$1;
    private final /* synthetic */ TL_messages_faveSticker f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$N40tY5XCjLdrkCjrt1TrAyUNxuI(MediaDataController mediaDataController, Object obj, TL_messages_faveSticker tL_messages_faveSticker) {
        this.f$0 = mediaDataController;
        this.f$1 = obj;
        this.f$2 = tL_messages_faveSticker;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$addRecentSticker$1$MediaDataController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
