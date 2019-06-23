package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$slEzQbDH_VVDpTJeXW32mMusC3c implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TL_messages_saveGif f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$slEzQbDH_VVDpTJeXW32mMusC3c(MediaDataController mediaDataController, TL_messages_saveGif tL_messages_saveGif) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$removeRecentGif$2$MediaDataController(this.f$1, tLObject, tL_error);
    }
}
