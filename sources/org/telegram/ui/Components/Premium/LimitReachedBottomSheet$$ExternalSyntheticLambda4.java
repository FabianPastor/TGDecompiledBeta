package org.telegram.ui.Components.Premium;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_messages_inactiveChats;

public final /* synthetic */ class LimitReachedBottomSheet$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ LimitReachedBottomSheet f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$TL_messages_inactiveChats f$2;

    public /* synthetic */ LimitReachedBottomSheet$$ExternalSyntheticLambda4(LimitReachedBottomSheet limitReachedBottomSheet, ArrayList arrayList, TLRPC$TL_messages_inactiveChats tLRPC$TL_messages_inactiveChats) {
        this.f$0 = limitReachedBottomSheet;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$TL_messages_inactiveChats;
    }

    public final void run() {
        this.f$0.lambda$loadInactiveChannels$9(this.f$1, this.f$2);
    }
}
