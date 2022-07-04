package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda122 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ TLRPC.StickerSet f$6;
    public final /* synthetic */ int f$7;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda122(MediaDataController mediaDataController, Context context, int i, BaseFragment baseFragment, boolean z, TLObject tLObject, TLRPC.StickerSet stickerSet, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = context;
        this.f$2 = i;
        this.f$3 = baseFragment;
        this.f$4 = z;
        this.f$5 = tLObject;
        this.f$6 = stickerSet;
        this.f$7 = i2;
    }

    public final void run() {
        this.f$0.m2111x98d64903(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
