package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$UAvqJ58O587JwQnJkDEqwebSXv4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MediaDataController$UAvqJ58O587JwQnJkDEqwebSXv4 INSTANCE = new -$$Lambda$MediaDataController$UAvqJ58O587JwQnJkDEqwebSXv4();

    private /* synthetic */ -$$Lambda$MediaDataController$UAvqJ58O587JwQnJkDEqwebSXv4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$27(tLObject, tL_error);
    }
}
