package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class StatisticActivity$ChartCell$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ StatisticActivity.ChartCell f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ StatisticActivity.ZoomCancelable f$2;

    public /* synthetic */ StatisticActivity$ChartCell$$ExternalSyntheticLambda1(StatisticActivity.ChartCell chartCell, String str, StatisticActivity.ZoomCancelable zoomCancelable) {
        this.f$0 = chartCell;
        this.f$1 = str;
        this.f$2 = zoomCancelable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onZoomed$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
