package org.telegram.ui.Components;

import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StickersAlert$$ExternalSyntheticLambda21 implements RequestDelegate {
    public final /* synthetic */ StickersAlert f$0;
    public final /* synthetic */ MediaDataController f$1;

    public /* synthetic */ StickersAlert$$ExternalSyntheticLambda21(StickersAlert stickersAlert, MediaDataController mediaDataController) {
        this.f$0 = stickersAlert;
        this.f$1 = mediaDataController;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2640lambda$loadStickerSet$6$orgtelegramuiComponentsStickersAlert(this.f$1, tLObject, tL_error);
    }
}
