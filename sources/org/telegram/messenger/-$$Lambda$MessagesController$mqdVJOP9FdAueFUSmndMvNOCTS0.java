package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$mqdVJOP9FdAueFUSmndMvNOCTS0 implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_messages_peerDialogs f$2;
    private final /* synthetic */ long f$3;
    private final /* synthetic */ LongSparseArray f$4;
    private final /* synthetic */ TL_messages_dialogs f$5;

    public /* synthetic */ -$$Lambda$MessagesController$mqdVJOP9FdAueFUSmndMvNOCTS0(MessagesController messagesController, int i, TL_messages_peerDialogs tL_messages_peerDialogs, long j, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_messages_peerDialogs;
        this.f$3 = j;
        this.f$4 = longSparseArray;
        this.f$5 = tL_messages_dialogs;
    }

    public final void run() {
        this.f$0.lambda$null$221$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
