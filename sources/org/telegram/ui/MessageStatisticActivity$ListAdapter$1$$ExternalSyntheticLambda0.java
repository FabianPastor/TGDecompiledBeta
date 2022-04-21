package org.telegram.ui;

import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.MessageStatisticActivity;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MessageStatisticActivity.ListAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ ChartData f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ StatisticActivity.ZoomCancelable f$3;

    public /* synthetic */ MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda0(MessageStatisticActivity.ListAdapter.AnonymousClass1 r1, ChartData chartData, String str, StatisticActivity.ZoomCancelable zoomCancelable) {
        this.f$0 = r1;
        this.f$1 = chartData;
        this.f$2 = str;
        this.f$3 = zoomCancelable;
    }

    public final void run() {
        this.f$0.m2632x952a21f2(this.f$1, this.f$2, this.f$3);
    }
}
