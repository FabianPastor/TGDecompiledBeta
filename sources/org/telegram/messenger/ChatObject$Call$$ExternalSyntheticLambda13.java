package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ChatObject.Call.OnParticipantsLoad f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ HashSet f$4;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda13(ChatObject.Call call, int i, ChatObject.Call.OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet) {
        this.f$0 = call;
        this.f$1 = i;
        this.f$2 = onParticipantsLoad;
        this.f$3 = arrayList;
        this.f$4 = hashSet;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUnknownParticipants$6(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
