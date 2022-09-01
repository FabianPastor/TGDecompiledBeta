package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda29 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda29(MediaDataController mediaDataController, int i, TLObject tLObject, BaseFragment baseFragment, boolean z, int i2) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = baseFragment;
        this.f$4 = z;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$toggleStickerSets$100(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
