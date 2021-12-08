package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda47 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda47(MediaDataController mediaDataController, TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_getFeaturedStickers;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m812xCLASSNAMEcCLASSNAME(this.f$1, tLObject, tL_error);
    }
}
