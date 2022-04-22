package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class StatisticActivity$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ StatisticActivity f$0;

    public /* synthetic */ StatisticActivity$$ExternalSyntheticLambda4(StatisticActivity statisticActivity) {
        this.f$0 = statisticActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadMessages$7(tLObject, tLRPC$TL_error);
    }
}
