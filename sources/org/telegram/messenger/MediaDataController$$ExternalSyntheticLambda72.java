package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda72 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.Document f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda72(MediaDataController mediaDataController, TLRPC.Document document) {
        this.f$0 = mediaDataController;
        this.f$1 = document;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2105x9CLASSNAMEa4e(this.f$1, tLObject, tL_error);
    }
}
