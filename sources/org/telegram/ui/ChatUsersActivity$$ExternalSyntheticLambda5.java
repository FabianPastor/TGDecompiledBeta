package org.telegram.ui;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AtomicInteger f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda5(ArrayList arrayList, int i, AtomicInteger atomicInteger, ArrayList arrayList2, Runnable runnable) {
        this.f$0 = arrayList;
        this.f$1 = i;
        this.f$2 = atomicInteger;
        this.f$3 = arrayList2;
        this.f$4 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new ChatUsersActivity$$ExternalSyntheticLambda15(tL_error, tLObject, this.f$0, this.f$1, this.f$2, this.f$3, this.f$4));
    }
}
