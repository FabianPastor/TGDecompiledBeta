package org.telegram.ui;

import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$ChartViewData$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StatisticActivity.ChartViewData f$0;
    public final /* synthetic */ ChartData f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ RecyclerListView f$3;
    public final /* synthetic */ StatisticActivity.DiffUtilsCallback f$4;

    public /* synthetic */ StatisticActivity$ChartViewData$$ExternalSyntheticLambda0(StatisticActivity.ChartViewData chartViewData, ChartData chartData, String str, RecyclerListView recyclerListView, StatisticActivity.DiffUtilsCallback diffUtilsCallback) {
        this.f$0 = chartViewData;
        this.f$1 = chartData;
        this.f$2 = str;
        this.f$3 = recyclerListView;
        this.f$4 = diffUtilsCallback;
    }

    public final void run() {
        this.f$0.m4632lambda$load$0$orgtelegramuiStatisticActivity$ChartViewData(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
