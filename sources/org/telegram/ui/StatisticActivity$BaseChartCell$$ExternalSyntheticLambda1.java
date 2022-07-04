package org.telegram.ui;

import android.animation.ValueAnimator;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$BaseChartCell$$ExternalSyntheticLambda1 implements ValueAnimator.AnimatorUpdateListener {
    public final /* synthetic */ StatisticActivity.BaseChartCell f$0;
    public final /* synthetic */ TransitionParams f$1;
    public final /* synthetic */ float f$2;

    public /* synthetic */ StatisticActivity$BaseChartCell$$ExternalSyntheticLambda1(StatisticActivity.BaseChartCell baseChartCell, TransitionParams transitionParams, float f) {
        this.f$0 = baseChartCell;
        this.f$1 = transitionParams;
        this.f$2 = f;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.f$0.m4622x8c3e0af5(this.f$1, this.f$2, valueAnimator);
    }
}
