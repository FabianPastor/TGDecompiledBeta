package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda144 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_messages_getFeaturedStickers f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda144(MediaDataController mediaDataController, TLObject tLObject, TLRPC.TL_messages_getFeaturedStickers tL_messages_getFeaturedStickers) {
        this.f$0 = mediaDataController;
        this.f$1 = tLObject;
        this.f$2 = tL_messages_getFeaturedStickers;
    }

    public final void run() {
        this.f$0.m739xdCLASSNAMEbCLASSNAME(this.f$1, this.f$2);
    }
}
