package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda19 implements MessagesController.ErrorDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda19(ChatRightsEditActivity chatRightsEditActivity) {
        this.f$0 = chatRightsEditActivity;
    }

    public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
        return this.f$0.lambda$onDonePressed$19(tLRPC$TL_error);
    }
}