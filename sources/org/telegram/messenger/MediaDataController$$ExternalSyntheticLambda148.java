package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda148 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda148(MediaDataController mediaDataController, int i, BaseFragment baseFragment, boolean z, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = z;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$toggleStickerSets$83(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
