package org.telegram.ui.Components;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;

public final /* synthetic */ class ChatAttachAlert$$ExternalSyntheticLambda9 implements ChatAttachAlertPollLayout.PollCreateActivityDelegate {
    public final /* synthetic */ ChatAttachAlert f$0;

    public /* synthetic */ ChatAttachAlert$$ExternalSyntheticLambda9(ChatAttachAlert chatAttachAlert) {
        this.f$0 = chatAttachAlert;
    }

    public final void sendPoll(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, HashMap hashMap, boolean z, int i) {
        this.f$0.m2104lambda$new$5$orgtelegramuiComponentsChatAttachAlert(tL_messageMediaPoll, hashMap, z, i);
    }
}
