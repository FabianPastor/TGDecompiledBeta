package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda36 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_messages_getMessages f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda36(MediaDataController mediaDataController, long j, TLRPC.TL_messages_getMessages tL_messages_getMessages) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tL_messages_getMessages;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m828x4var_dd3(this.f$1, this.f$2, tLObject, tL_error);
    }
}
