package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_messages_inactiveChats;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TooManyCommunitiesActivity$Z0alkQb4Er7umg5vNcQyX2-gLug implements Runnable {
    private final /* synthetic */ TooManyCommunitiesActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ TL_messages_inactiveChats f$2;

    public /* synthetic */ -$$Lambda$TooManyCommunitiesActivity$Z0alkQb4Er7umg5vNcQyX2-gLug(TooManyCommunitiesActivity tooManyCommunitiesActivity, ArrayList arrayList, TL_messages_inactiveChats tL_messages_inactiveChats) {
        this.f$0 = tooManyCommunitiesActivity;
        this.f$1 = arrayList;
        this.f$2 = tL_messages_inactiveChats;
    }

    public final void run() {
        this.f$0.lambda$null$4$TooManyCommunitiesActivity(this.f$1, this.f$2);
    }
}
