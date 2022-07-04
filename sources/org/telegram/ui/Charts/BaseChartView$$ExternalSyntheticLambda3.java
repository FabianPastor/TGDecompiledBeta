package org.telegram.ui.Charts;

import android.animation.ValueAnimator;
import org.telegram.ui.Charts.view_data.ChartHorizontalLinesData;

public final /* synthetic */ class BaseChartView$$ExternalSyntheticLambda3 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ BaseChartView f$0;
    public final /* synthetic */ ChartHorizontalLinesData f$1;

    public /* synthetic */ BaseChartView$$ExternalSyntheticLambda3(BaseChartView baseChartView, ChartHorizontalLinesData chartHorizontalLinesData) {
        this.f$0 = baseChartView;
        this.f$1 = chartHorizontalLinesData;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m2902lambda$setMaxMinValue$2$orgtelegramuiChartsBaseChartView(this.f$1, valueAnimator);
    }
}
