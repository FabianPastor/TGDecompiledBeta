package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$111 implements RequestDelegate {
    private final MessagesController arg$1;
    private final FileLocation arg$2;
    private final FileLocation arg$3;

    MessagesController$$Lambda$111(MessagesController messagesController, FileLocation fileLocation, FileLocation fileLocation2) {
        this.arg$1 = messagesController;
        this.arg$2 = fileLocation;
        this.arg$3 = fileLocation2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$changeChatAvatar$173$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
