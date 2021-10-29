package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ TLRPC$TL_messages_importChatInvite f$1;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda8(JoinGroupAlert joinGroupAlert, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tLRPC$TL_messages_importChatInvite;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$7(this.f$1, tLObject, tLRPC$TL_error);
    }
}
