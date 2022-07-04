package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ReactedHeaderView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ReactedHeaderView f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.Chat f$2;

    public /* synthetic */ ReactedHeaderView$$ExternalSyntheticLambda5(ReactedHeaderView reactedHeaderView, long j, TLRPC.Chat chat) {
        this.f$0 = reactedHeaderView;
        this.f$1 = j;
        this.f$2 = chat;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1288xe1027737(this.f$1, this.f$2, tLObject, tL_error);
    }
}
