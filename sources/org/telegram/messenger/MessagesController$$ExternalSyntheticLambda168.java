package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda168 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.TL_help_promoData f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda168(MessagesController messagesController, int i, TLRPC.TL_help_promoData tL_help_promoData, long j) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_help_promoData;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m144xCLASSNAME(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
