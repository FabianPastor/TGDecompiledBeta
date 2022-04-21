package org.telegram.ui;

import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$ChartCell$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ StatisticActivity.ChartCell f$0;
    public final /* synthetic */ ChartData f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ StatisticActivity.ZoomCancelable f$3;

    public /* synthetic */ StatisticActivity$ChartCell$$ExternalSyntheticLambda0(StatisticActivity.ChartCell chartCell, ChartData chartData, String str, StatisticActivity.ZoomCancelable zoomCancelable) {
        this.f$0 = chartCell;
        this.f$1 = chartData;
        this.f$2 = str;
        this.f$3 = zoomCancelable;
    }

    public final void run() {
        this.f$0.m3267lambda$onZoomed$0$orgtelegramuiStatisticActivity$ChartCell(this.f$1, this.f$2, this.f$3);
    }
}
