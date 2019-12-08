package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$8QP1AswA3oyMrrhmrghvwHW8OL4 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ TL_messages_search f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ long f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ long f$8;
    private final /* synthetic */ User f$9;

    public /* synthetic */ -$$Lambda$MediaDataController$8QP1AswA3oyMrrhmrghvwHW8OL4(MediaDataController mediaDataController, int i, boolean z, TLObject tLObject, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = tL_messages_search;
        this.f$5 = j;
        this.f$6 = j2;
        this.f$7 = i2;
        this.f$8 = j3;
        this.f$9 = user;
    }

    public final void run() {
        this.f$0.lambda$null$51$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
    }
}
