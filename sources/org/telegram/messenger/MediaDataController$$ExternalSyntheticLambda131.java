package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda131 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean[] f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLObject f$6;
    public final /* synthetic */ TLRPC$StickerSet f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda131(MediaDataController mediaDataController, boolean[] zArr, Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC$StickerSet tLRPC$StickerSet, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = zArr;
        this.f$2 = context;
        this.f$3 = i;
        this.f$4 = baseFragment;
        this.f$5 = z;
        this.f$6 = tLObject;
        this.f$7 = tLRPC$StickerSet;
        this.f$8 = i2;
    }

    public final void run() {
        this.f$0.lambda$toggleStickerSet$91(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
