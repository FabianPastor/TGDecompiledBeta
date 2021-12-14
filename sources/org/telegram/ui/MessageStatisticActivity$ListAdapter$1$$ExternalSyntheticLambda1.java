package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.MessageStatisticActivity;
import org.telegram.ui.StatisticActivity;

public final /* synthetic */ class MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ MessageStatisticActivity.ListAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ StatisticActivity.ZoomCancelable f$2;

    public /* synthetic */ MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda1(MessageStatisticActivity.ListAdapter.AnonymousClass1 r1, String str, StatisticActivity.ZoomCancelable zoomCancelable) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = zoomCancelable;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onZoomed$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
