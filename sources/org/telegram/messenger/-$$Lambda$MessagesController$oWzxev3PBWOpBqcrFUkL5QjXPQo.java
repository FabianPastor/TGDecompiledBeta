package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$oWzxev3PBWOpBqcrFUkL5QjXPQo implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ messages_Dialogs f$1;
    private final /* synthetic */ LongSparseArray f$2;
    private final /* synthetic */ LongSparseArray f$3;
    private final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ -$$Lambda$MessagesController$oWzxev3PBWOpBqcrFUkL5QjXPQo(MessagesController messagesController, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        this.f$0 = messagesController;
        this.f$1 = messages_dialogs;
        this.f$2 = longSparseArray;
        this.f$3 = longSparseArray2;
        this.f$4 = longSparseArray3;
    }

    public final void run() {
        this.f$0.lambda$null$137$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
