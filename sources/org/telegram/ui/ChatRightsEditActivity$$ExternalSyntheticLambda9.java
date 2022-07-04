package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda9 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda9(ChatRightsEditActivity chatRightsEditActivity) {
        this.f$0 = chatRightsEditActivity;
    }

    public final boolean run(TLRPC.TL_error tL_error) {
        return this.f$0.m3291lambda$onDonePressed$19$orgtelegramuiChatRightsEditActivity(tL_error);
    }
}
