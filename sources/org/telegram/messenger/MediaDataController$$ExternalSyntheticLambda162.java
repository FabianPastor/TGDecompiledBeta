package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda162 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC$TL_channels_getMessages f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda162(MediaDataController mediaDataController, long j, long j2, TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = tLRPC$TL_channels_getMessages;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPinnedMessageInternal$128(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
