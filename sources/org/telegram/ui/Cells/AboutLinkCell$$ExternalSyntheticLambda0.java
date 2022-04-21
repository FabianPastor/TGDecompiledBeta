package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.ui.Cells.AboutLinkCell;

public final /* synthetic */ class AboutLinkCell$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ AboutLinkCell f$0;
    public final /* synthetic */ AtomicReference f$1;
    public final /* synthetic */ float f$2;
    public final /* synthetic */ float f$3;
    public final /* synthetic */ AboutLinkCell.SpringInterpolator f$4;

    public /* synthetic */ AboutLinkCell$$ExternalSyntheticLambda0(AboutLinkCell aboutLinkCell, AtomicReference atomicReference, float f, float f2, AboutLinkCell.SpringInterpolator springInterpolator) {
        this.f$0 = aboutLinkCell;
        this.f$1 = atomicReference;
        this.f$2 = f;
        this.f$3 = f2;
        this.f$4 = springInterpolator;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m1483lambda$updateCollapse$1$orgtelegramuiCellsAboutLinkCell(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
    }
}
