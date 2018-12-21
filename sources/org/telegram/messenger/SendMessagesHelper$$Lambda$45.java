package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;

final /* synthetic */ class SendMessagesHelper$$Lambda$45 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final TL_error arg$2;
    private final ArrayList arg$3;
    private final TL_messages_sendMultiMedia arg$4;
    private final ArrayList arg$5;
    private final ArrayList arg$6;
    private final DelayedMessage arg$7;
    private final TLObject arg$8;

    SendMessagesHelper$$Lambda$45(SendMessagesHelper sendMessagesHelper, TL_error tL_error, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = tL_error;
        this.arg$3 = arrayList;
        this.arg$4 = tL_messages_sendMultiMedia;
        this.arg$5 = arrayList2;
        this.arg$6 = arrayList3;
        this.arg$7 = delayedMessage;
        this.arg$8 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$29$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
