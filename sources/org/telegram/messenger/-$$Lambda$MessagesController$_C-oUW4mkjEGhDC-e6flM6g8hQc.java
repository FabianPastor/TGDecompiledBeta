package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$_C-oUW4mkjEGhDC-e6flM6g8hQc implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ messages_Dialogs f$1;
    private final /* synthetic */ LongSparseArray f$2;
    private final /* synthetic */ LongSparseArray f$3;

    public /* synthetic */ -$$Lambda$MessagesController$_C-oUW4mkjEGhDC-e6flM6g8hQc(MessagesController messagesController, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$null$123$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}