package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_help_promoData;
import org.telegram.tgnet.TLRPC$TL_messages_peerDialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda158 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_help_promoData f$1;
    public final /* synthetic */ TLRPC$TL_messages_peerDialogs f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda158(MessagesController messagesController, TLRPC$TL_help_promoData tLRPC$TL_help_promoData, TLRPC$TL_messages_peerDialogs tLRPC$TL_messages_peerDialogs, long j) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_help_promoData;
        this.f$2 = tLRPC$TL_messages_peerDialogs;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$checkPromoInfoInternal$127(this.f$1, this.f$2, this.f$3);
    }
}
