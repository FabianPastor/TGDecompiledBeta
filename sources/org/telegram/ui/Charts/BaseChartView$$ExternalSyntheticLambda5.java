package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import org.telegram.ui.Charts.view_data.LineViewData;

public final /* synthetic */ class BaseChartView$$ExternalSyntheticLambda5 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BaseChartView f$0;
    public final /* synthetic */ LineViewData f$1;

    public /* synthetic */ BaseChartView$$ExternalSyntheticLambda5(BaseChartView baseChartView, LineViewData lineViewData) {
        this.f$0 = baseChartView;
        this.f$1 = lineViewData;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m1625lambda$onCheckChanged$5$orgtelegramuiChartsBaseChartView(this.f$1, valueAnimator);
    }
}
