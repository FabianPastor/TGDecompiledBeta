package org.telegram.ui;

import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ StatisticActivity f$0;
    public final /* synthetic */ StatisticActivity.ChartViewData[] f$1;

    public /* synthetic */ StatisticActivity$$ExternalSyntheticLambda2(StatisticActivity statisticActivity, StatisticActivity.ChartViewData[] chartViewDataArr) {
        this.f$0 = statisticActivity;
        this.f$1 = chartViewDataArr;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0(this.f$1);
    }
}