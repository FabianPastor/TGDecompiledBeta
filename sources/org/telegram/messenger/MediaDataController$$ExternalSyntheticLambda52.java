package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda52 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_messages_search f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda52(MediaDataController mediaDataController, int i, TLRPC.TL_messages_search tL_messages_search, long j, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = tL_messages_search;
        this.f$3 = j;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2031x965d941d(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
