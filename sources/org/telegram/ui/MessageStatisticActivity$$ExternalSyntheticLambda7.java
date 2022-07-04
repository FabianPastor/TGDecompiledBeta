package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ MessageStatisticActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.TL_stats_loadAsyncGraph f$2;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda7(MessageStatisticActivity messageStatisticActivity, String str, TLRPC.TL_stats_loadAsyncGraph tL_stats_loadAsyncGraph) {
        this.f$0 = messageStatisticActivity;
        this.f$1 = str;
        this.f$2 = tL_stats_loadAsyncGraph;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3956lambda$loadStat$6$orgtelegramuiMessageStatisticActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
