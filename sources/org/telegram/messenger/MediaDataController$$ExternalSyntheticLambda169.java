package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda169 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.StickerSet f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ TLRPC.TL_error f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ Context f$8;
    public final /* synthetic */ TLObject f$9;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda169(MediaDataController mediaDataController, TLRPC.StickerSet stickerSet, TLObject tLObject, BaseFragment baseFragment, boolean z, int i, TLRPC.TL_error tL_error, boolean z2, Context context, TLObject tLObject2) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
        this.f$2 = tLObject;
        this.f$3 = baseFragment;
        this.f$4 = z;
        this.f$5 = i;
        this.f$6 = tL_error;
        this.f$7 = z2;
        this.f$8 = context;
        this.f$9 = tLObject2;
    }

    public final void run() {
        this.f$0.m2112x189eec7f(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
