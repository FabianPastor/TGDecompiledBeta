package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ TL_updates f$1;

    public /* synthetic */ -$$Lambda$GcmPushListenerService$sNJBSY9wYQGtvc_jgrXU5q2KmRo(int i, TL_updates tL_updates) {
        this.f$0 = i;
        this.f$1 = tL_updates;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).processUpdates(this.f$1, false);
    }
}
