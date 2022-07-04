package org.telegram.ui.Charts;

import android.animation.ValueAnimator;

public final /* synthetic */ class PieChartView$$ExternalSyntheticLambda0 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ PieChartView f$0;
    public final /* synthetic */ PieChartViewData f$1;

    public /* synthetic */ PieChartView$$ExternalSyntheticLambda0(PieChartView pieChartView, PieChartViewData pieChartViewData) {
        this.f$0 = pieChartView;
        this.f$1 = pieChartViewData;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2906lambda$updateCharValues$0$orgtelegramuiChartsPieChartView(this.f$1, valueAnimator);
    }
}
