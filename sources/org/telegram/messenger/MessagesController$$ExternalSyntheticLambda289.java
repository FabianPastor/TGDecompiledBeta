package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda289 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda289(MessagesController messagesController, long j, ArrayList arrayList) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = arrayList;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkUnreadReactions$323(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
