package org.telegram.ui;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ TLRPC$TL_error f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ AtomicInteger f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ Runnable f$6;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda6(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, int i, AtomicInteger atomicInteger, ArrayList arrayList2, Runnable runnable) {
        this.f$0 = tLRPC$TL_error;
        this.f$1 = tLObject;
        this.f$2 = arrayList;
        this.f$3 = i;
        this.f$4 = atomicInteger;
        this.f$5 = arrayList2;
        this.f$6 = runnable;
    }

    public final void run() {
        ChatUsersActivity.lambda$loadChatParticipants$15(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
