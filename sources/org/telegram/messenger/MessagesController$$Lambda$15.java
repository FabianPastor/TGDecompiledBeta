package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$15 implements RequestDelegate {
    private final MessagesController arg$1;
    private final long arg$2;
    private final Chat arg$3;
    private final ArrayList arg$4;

    MessagesController$$Lambda$15(MessagesController messagesController, long j, Chat chat, ArrayList arrayList) {
        this.arg$1 = messagesController;
        this.arg$2 = j;
        this.arg$3 = chat;
        this.arg$4 = arrayList;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$reloadMessages$20$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
