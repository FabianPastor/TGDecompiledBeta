package org.telegram.ui;

import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ PhotoViewer.AnonymousClass12 f$0;
    public final /* synthetic */ UserConfig f$1;
    public final /* synthetic */ TLRPC$Photo f$2;

    public /* synthetic */ PhotoViewer$12$$ExternalSyntheticLambda8(PhotoViewer.AnonymousClass12 r1, UserConfig userConfig, TLRPC$Photo tLRPC$Photo) {
        this.f$0 = r1;
        this.f$1 = userConfig;
        this.f$2 = tLRPC$Photo;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onItemClick$8(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}