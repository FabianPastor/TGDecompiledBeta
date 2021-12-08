package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$ImportingHistory$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.ImportingHistory.AnonymousClass1 f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_messages_initHistoryImport f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;

    public /* synthetic */ SendMessagesHelper$ImportingHistory$1$$ExternalSyntheticLambda0(SendMessagesHelper.ImportingHistory.AnonymousClass1 r1, TLObject tLObject, TLRPC.TL_messages_initHistoryImport tL_messages_initHistoryImport, TLRPC.TL_error tL_error) {
        this.f$0 = r1;
        this.f$1 = tLObject;
        this.f$2 = tL_messages_initHistoryImport;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.m1157x6fa5ea93(this.f$1, this.f$2, this.f$3);
    }
}
