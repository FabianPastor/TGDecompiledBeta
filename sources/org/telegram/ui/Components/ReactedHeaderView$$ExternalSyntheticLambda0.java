package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;

public final /* synthetic */ class ReactedHeaderView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ReactedHeaderView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_messages_messageReactionsList f$2;

    public /* synthetic */ ReactedHeaderView$$ExternalSyntheticLambda0(ReactedHeaderView reactedHeaderView, int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        this.f$0 = reactedHeaderView;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_messages_messageReactionsList;
    }

    public final void run() {
        this.f$0.lambda$loadReactions$6(this.f$1, this.f$2);
    }
}
