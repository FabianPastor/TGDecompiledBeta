package org.telegram.messenger;

import android.util.LongSparseArray;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$iLPp_yBkE-A4gt0g9ezdFE0ctRI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ LongSparseArray f$1;
    private final /* synthetic */ LongSparseArray f$2;

    public /* synthetic */ -$$Lambda$MessagesController$iLPp_yBkE-A4gt0g9ezdFE0ctRI(MessagesController messagesController, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = longSparseArray;
        this.f$2 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$updatePrintingStrings$111$MessagesController(this.f$1, this.f$2);
    }
}
