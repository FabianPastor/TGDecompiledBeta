package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class StatisticActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ StatisticActivity f$0;

    public /* synthetic */ StatisticActivity$$ExternalSyntheticLambda4(StatisticActivity statisticActivity) {
        this.f$0 = statisticActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3255lambda$loadMessages$7$orgtelegramuiStatisticActivity(tLObject, tL_error);
    }
}
