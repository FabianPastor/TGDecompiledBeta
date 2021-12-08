package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.Chat f$3;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda5(MessageSeenView messageSeenView, long j, int i, TLRPC.Chat chat) {
        this.f$0 = messageSeenView;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = chat;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3299lambda$new$5$orgtelegramuiMessageSeenView(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
