package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda206 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda206(MessagesController messagesController, long j, boolean z, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = arrayList;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m359x7d10d185(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
