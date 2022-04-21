package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$ImportingHistory$3$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.ImportingHistory.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_messages_startHistoryImport f$2;

    public /* synthetic */ SendMessagesHelper$ImportingHistory$3$$ExternalSyntheticLambda0(SendMessagesHelper.ImportingHistory.AnonymousClass3 r1, TLRPC.TL_error tL_error, TLRPC.TL_messages_startHistoryImport tL_messages_startHistoryImport) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tL_messages_startHistoryImport;
    }

    public final void run() {
        this.f$0.m1101x6fa5ea95(this.f$1, this.f$2);
    }
}
