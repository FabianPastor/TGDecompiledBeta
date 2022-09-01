package org.telegram.messenger;

import android.content.Context;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda132 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean[] f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ Context f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda132(MediaDataController mediaDataController, boolean[] zArr, ArrayList arrayList, Context context, BaseFragment baseFragment, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = zArr;
        this.f$2 = arrayList;
        this.f$3 = context;
        this.f$4 = baseFragment;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$removeMultipleStickerSets$93(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
