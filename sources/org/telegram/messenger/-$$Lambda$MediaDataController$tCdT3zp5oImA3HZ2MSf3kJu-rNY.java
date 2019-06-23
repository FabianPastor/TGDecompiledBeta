package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_getFeaturedStickers;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$tCdT3zp5oImA3HZ2MSf3kJu-rNY implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_messages_getFeaturedStickers f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$tCdT3zp5oImA3HZ2MSf3kJu-rNY(MediaDataController mediaDataController, TLObject tLObject, TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = tL_messages_getFeaturedStickers;
    }

    public final void run() {
        this.f$0.lambda$null$18$MediaDataController(this.f$1, this.f$2);
    }
}
