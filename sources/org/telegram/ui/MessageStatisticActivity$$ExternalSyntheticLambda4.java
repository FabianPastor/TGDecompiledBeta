package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_stats_loadAsyncGraph;
import org.telegram.ui.Charts.data.ChartData;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ MessageStatisticActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ ChartData f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$TL_stats_loadAsyncGraph f$4;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda4(MessageStatisticActivity messageStatisticActivity, TLRPC$TL_error tLRPC$TL_error, ChartData chartData, String str, TLRPC$TL_stats_loadAsyncGraph tLRPC$TL_stats_loadAsyncGraph) {
        this.f$0 = messageStatisticActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = chartData;
        this.f$3 = str;
        this.f$4 = tLRPC$TL_stats_loadAsyncGraph;
    }

    public final void run() {
        this.f$0.lambda$loadStat$5(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}