package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$r00eBxvrVBisvd_A6PdgHzlHuc4 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_help_proxyDataPromo f$1;
    private final /* synthetic */ TL_messages_peerDialogs f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$r00eBxvrVBisvd_A6PdgHzlHuc4(MessagesController messagesController, TL_help_proxyDataPromo tL_help_proxyDataPromo, TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        this.f$0 = messagesController;
        this.f$1 = tL_help_proxyDataPromo;
        this.f$2 = tL_messages_peerDialogs;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$null$106$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
