package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$ChartViewData$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ StatisticActivity.ChartViewData f$0;
    public final /* synthetic */ RecyclerListView f$1;
    public final /* synthetic */ StatisticActivity.DiffUtilsCallback f$2;

    public /* synthetic */ StatisticActivity$ChartViewData$$ExternalSyntheticLambda1(StatisticActivity.ChartViewData chartViewData, RecyclerListView recyclerListView, StatisticActivity.DiffUtilsCallback diffUtilsCallback) {
        this.f$0 = chartViewData;
        this.f$1 = recyclerListView;
        this.f$2 = diffUtilsCallback;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$load$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}