package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$47 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Integer arg$2;

    MessagesController$$Lambda$47(MessagesController messagesController, Integer num) {
        this.arg$1 = messagesController;
        this.arg$2 = num;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadChannelParticipants$63$MessagesController(this.arg$2, tLObject, tL_error);
    }
}
