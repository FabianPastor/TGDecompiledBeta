package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$ZoVPUyusoxq6AeVwCvLbTk3Ha-M implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TL_messages_search f$3;
    private final /* synthetic */ long f$4;
    private final /* synthetic */ long f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ long f$7;
    private final /* synthetic */ User f$8;

    public /* synthetic */ -$$Lambda$MediaDataController$ZoVPUyusoxq6AeVwCvLbTk3Ha-M(MediaDataController mediaDataController, int i, boolean z, TL_messages_search tL_messages_search, long j, long j2, int i2, long j3, User user) {
        this.f$0 = mediaDataController;
        this.f$1 = i;
        this.f$2 = z;
        this.f$3 = tL_messages_search;
        this.f$4 = j;
        this.f$5 = j2;
        this.f$6 = i2;
        this.f$7 = j3;
        this.f$8 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchMessagesInChat$52$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
    }
}