package org.telegram.ui;

import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass12 f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ UserConfig f$2;
    public final /* synthetic */ TLRPC$Photo f$3;

    public /* synthetic */ PhotoViewer$12$$ExternalSyntheticLambda3(PhotoViewer.AnonymousClass12 r1, TLObject tLObject, UserConfig userConfig, TLRPC$Photo tLRPC$Photo) {
        this.f$0 = r1;
        this.f$1 = tLObject;
        this.f$2 = userConfig;
        this.f$3 = tLRPC$Photo;
    }

    public final void run() {
        this.f$0.lambda$onItemClick$4(this.f$1, this.f$2, this.f$3);
    }
}