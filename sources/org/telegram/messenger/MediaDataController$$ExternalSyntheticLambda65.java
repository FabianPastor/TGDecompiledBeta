package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda65 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_messages_saveGif f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda65(MediaDataController mediaDataController, TLRPC.TL_messages_saveGif tL_messages_saveGif) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m815x800332(this.f$1, tLObject, tL_error);
    }
}
