package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda205 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda205 INSTANCE = new MediaDataController$$ExternalSyntheticLambda205();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda205() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$132(tLObject, tLRPC$TL_error);
    }
}
