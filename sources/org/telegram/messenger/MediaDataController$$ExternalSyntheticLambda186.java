package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda186 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_messages_getMessages f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda186(MediaDataController mediaDataController, long j, TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_messages_getMessages;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPinnedMessageInternal$143(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
