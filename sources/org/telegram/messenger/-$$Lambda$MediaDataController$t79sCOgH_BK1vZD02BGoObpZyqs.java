package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$t79sCOgH_BK1vZD02BGoObpZyqs implements RequestDelegate {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ TL_messages_search f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ User f$6;
    private final /* synthetic */ boolean f$7;

    public /* synthetic */ -$$Lambda$MediaDataController$t79sCOgH_BK1vZD02BGoObpZyqs(MediaDataController mediaDataController, long j, TL_messages_search tL_messages_search, long j2, int i, int i2, User user, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = tL_messages_search;
        this.f$3 = j2;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = user;
        this.f$7 = z;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchMessagesInChat$51$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
    }
}
