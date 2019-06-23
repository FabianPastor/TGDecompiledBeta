package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$iOG-1qJGc2XFef--4F0nPFq0WTw implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ StickerSet f$1;
    private final /* synthetic */ Context f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$iOG-1qJGc2XFef--4F0nPFq0WTw(MediaDataController mediaDataController, StickerSet stickerSet, Context context, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
        this.f$2 = context;
        this.f$3 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$removeStickersSet$46$MediaDataController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
