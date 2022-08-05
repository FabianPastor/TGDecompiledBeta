package org.telegram.ui;

import android.content.Context;
import org.telegram.ui.DefaultThemesPreviewCell;

public final /* synthetic */ class DefaultThemesPreviewCell$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DefaultThemesPreviewCell.AnonymousClass1 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ DefaultThemesPreviewCell$1$$ExternalSyntheticLambda0(DefaultThemesPreviewCell.AnonymousClass1 r1, int i, Context context, int i2, boolean z) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = context;
        this.f$3 = i2;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$onClick$0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
