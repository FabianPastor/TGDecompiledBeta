package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

final /* synthetic */ class SendMessagesHelper$$Lambda$10 implements RequestDelegate {
    private final SendMessagesHelper arg$1;
    private final ArrayList arg$2;
    private final TL_messages_sendMultiMedia arg$3;
    private final ArrayList arg$4;
    private final ArrayList arg$5;
    private final DelayedMessage arg$6;

    SendMessagesHelper$$Lambda$10(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = arrayList;
        this.arg$3 = tL_messages_sendMultiMedia;
        this.arg$4 = arrayList2;
        this.arg$5 = arrayList3;
        this.arg$6 = delayedMessage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$performSendMessageRequestMulti$28$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, tLObject, tL_error);
    }
}
