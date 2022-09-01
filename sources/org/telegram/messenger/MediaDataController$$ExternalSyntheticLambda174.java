package org.telegram.messenger;

import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda174 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Utilities.Callback f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda174(MediaDataController mediaDataController, int i, Utilities.Callback callback) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = callback;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadStickers$79(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
