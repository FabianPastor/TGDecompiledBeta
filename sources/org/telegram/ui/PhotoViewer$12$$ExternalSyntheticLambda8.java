package org.telegram.ui;

import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ PhotoViewer.AnonymousClass12 f$0;
    public final /* synthetic */ UserConfig f$1;
    public final /* synthetic */ TLRPC.Photo f$2;

    public /* synthetic */ PhotoViewer$12$$ExternalSyntheticLambda8(PhotoViewer.AnonymousClass12 r1, UserConfig userConfig, TLRPC.Photo photo) {
        this.f$0 = r1;
        this.f$1 = userConfig;
        this.f$2 = photo;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4308lambda$onItemClick$8$orgtelegramuiPhotoViewer$12(this.f$1, this.f$2, tLObject, tL_error);
    }
}
