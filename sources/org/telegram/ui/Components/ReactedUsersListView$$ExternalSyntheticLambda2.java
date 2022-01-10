package org.telegram.ui.Components;

import java.util.List;
import org.telegram.tgnet.TLRPC$TL_messages_messageReactionsList;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ ReactedUsersListView f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$TL_messages_messageReactionsList f$3;

    public /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda2(ReactedUsersListView reactedUsersListView, List list, int i, TLRPC$TL_messages_messageReactionsList tLRPC$TL_messages_messageReactionsList) {
        this.f$0 = reactedUsersListView;
        this.f$1 = list;
        this.f$2 = i;
        this.f$3 = tLRPC$TL_messages_messageReactionsList;
    }

    public final void run() {
        this.f$0.lambda$load$4(this.f$1, this.f$2, this.f$3);
    }
}
