package org.telegram.messenger;

import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.ChatObject;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatObject$Call$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ ChatObject.Call.OnParticipantsLoad f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ HashSet f$5;

    public /* synthetic */ ChatObject$Call$$ExternalSyntheticLambda9(ChatObject.Call call, int i, TLObject tLObject, ChatObject.Call.OnParticipantsLoad onParticipantsLoad, ArrayList arrayList, HashSet hashSet) {
        this.f$0 = call;
        this.f$1 = i;
        this.f$2 = tLObject;
        this.f$3 = onParticipantsLoad;
        this.f$4 = arrayList;
        this.f$5 = hashSet;
    }

    public final void run() {
        this.f$0.m1786xe351e81a(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
