package org.telegram.messenger;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda166 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Utilities.Callback f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda166(MediaDataController mediaDataController, int i, Utilities.Callback callback, long j) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = callback;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadStickers$80(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
