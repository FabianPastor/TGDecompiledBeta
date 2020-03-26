package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$TApLYOo58P6mm1cfUimaeUr8LI0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$TApLYOo58P6mm1cfUimaeUr8LI0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$TApLYOo58P6mm1cfUimaeUr8LI0 INSTANCE = new $$Lambda$MediaDataController$TApLYOo58P6mm1cfUimaeUr8LI0();

    private /* synthetic */ $$Lambda$MediaDataController$TApLYOo58P6mm1cfUimaeUr8LI0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$30(tLObject, tLRPC$TL_error);
    }
}
