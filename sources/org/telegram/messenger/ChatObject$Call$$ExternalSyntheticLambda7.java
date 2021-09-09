package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda7 implements Comparator {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda7(ChatObject.Call call, long j, boolean z) {
        this.f$0 = call;
        this.f$1 = j;
        this.f$2 = z;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$sortParticipants$11(this.f$1, this.f$2, (TLRPC$TL_groupCallParticipant) obj, (TLRPC$TL_groupCallParticipant) obj2);
    }
}
