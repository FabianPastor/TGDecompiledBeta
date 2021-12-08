package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda39 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC.TL_messages_saveRecentSticker f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda39(MediaDataController mediaDataController, Object obj, TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker) {
        this.f$0 = mediaDataController;
        this.f$1 = obj;
        this.f$2 = tL_messages_saveRecentSticker;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m774xb78796c4(this.f$1, this.f$2, tLObject, tL_error);
    }
}
