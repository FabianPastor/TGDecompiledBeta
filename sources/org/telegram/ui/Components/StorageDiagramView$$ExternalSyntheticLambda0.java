package org.telegram.ui.Components;

import android.animation.ValueAnimator;
import org.telegram.ui.Components.StorageDiagramView;

public final /* synthetic */ class StorageDiagramView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ StorageDiagramView f$0;
    public final /* synthetic */ StorageDiagramView.ClearViewData[] f$1;

    public /* synthetic */ StorageDiagramView$$ExternalSyntheticLambda0(StorageDiagramView storageDiagramView, StorageDiagramView.ClearViewData[] clearViewDataArr) {
        this.f$0 = storageDiagramView;
        this.f$1 = clearViewDataArr;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2659lambda$update$0$orgtelegramuiComponentsStorageDiagramView(this.f$1, valueAnimator);
    }
}
