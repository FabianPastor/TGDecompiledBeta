package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda34 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.TL_channels_getMessages f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda34(MediaDataController mediaDataController, long j, long j2, TLRPC.TL_channels_getMessages tL_channels_getMessages) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = tL_channels_getMessages;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m827x8CLASSNAMEe474(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
