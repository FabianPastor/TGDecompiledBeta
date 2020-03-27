package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$03_yXj93NmYjr7N9Yky5sc7MnNY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$03_yXj93NmYjr7N9Yky5sc7MnNY implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$03_yXj93NmYjr7N9Yky5sc7MnNY INSTANCE = new $$Lambda$MediaDataController$03_yXj93NmYjr7N9Yky5sc7MnNY();

    private /* synthetic */ $$Lambda$MediaDataController$03_yXj93NmYjr7N9Yky5sc7MnNY() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersByIdAsRead$31(tLObject, tLRPC$TL_error);
    }
}
