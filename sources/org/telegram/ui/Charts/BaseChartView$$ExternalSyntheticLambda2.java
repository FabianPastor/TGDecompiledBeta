package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import org.telegram.ui.Charts.view_data.ChartBottomSignatureData;

public final /* synthetic */ class BaseChartView$$ExternalSyntheticLambda2 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BaseChartView f$0;
    public final /* synthetic */ ChartBottomSignatureData f$1;

    public /* synthetic */ BaseChartView$$ExternalSyntheticLambda2(BaseChartView baseChartView, ChartBottomSignatureData chartBottomSignatureData) {
        this.f$0 = baseChartView;
        this.f$1 = chartBottomSignatureData;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.lambda$updateDates$3(this.f$1, valueAnimator);
    }
}
