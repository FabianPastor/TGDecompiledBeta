package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda167 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$StickerSet f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ Context f$6;
    public final /* synthetic */ TLObject f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda167(MediaDataController mediaDataController, TLRPC$StickerSet tLRPC$StickerSet, BaseFragment baseFragment, boolean z, int i, boolean z2, Context context, TLObject tLObject) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$StickerSet;
        this.f$2 = baseFragment;
        this.f$3 = z;
        this.f$4 = i;
        this.f$5 = z2;
        this.f$6 = context;
        this.f$7 = tLObject;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$toggleStickerSetInternal$79(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tLRPC$TL_error);
    }
}
