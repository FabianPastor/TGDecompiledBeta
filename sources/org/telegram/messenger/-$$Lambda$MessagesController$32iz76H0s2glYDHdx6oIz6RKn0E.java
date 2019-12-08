package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$32iz76H0s2glYDHdx6oIz6RKn0E implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ messages_Dialogs f$4;
    private final /* synthetic */ LongSparseArray f$5;
    private final /* synthetic */ LongSparseArray f$6;

    public /* synthetic */ -$$Lambda$MessagesController$32iz76H0s2glYDHdx6oIz6RKn0E(MessagesController messagesController, int i, int i2, int i3, messages_Dialogs messages_dialogs, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = i3;
        this.f$4 = messages_dialogs;
        this.f$5 = longSparseArray;
        this.f$6 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$completeDialogsReset$138$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
