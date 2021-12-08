package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda38 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC.TL_messages_faveSticker f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda38(MediaDataController mediaDataController, Object obj, TLRPC.TL_messages_faveSticker tL_messages_faveSticker) {
        this.f$0 = mediaDataController;
        this.f$1 = obj;
        this.f$2 = tL_messages_faveSticker;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m773xvar_b2d65(this.f$1, this.f$2, tLObject, tL_error);
    }
}
