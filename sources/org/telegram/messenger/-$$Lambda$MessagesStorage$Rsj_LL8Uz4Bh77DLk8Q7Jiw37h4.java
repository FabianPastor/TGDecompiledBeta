package org.telegram.messenger;

import android.util.LongSparseArray;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.messages_Dialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$Rsj_LL8Uz4Bh77DLk8Q7Jiw37h4 implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ messages_Dialogs f$1;
    private final /* synthetic */ LongSparseArray f$10;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ Message f$7;
    private final /* synthetic */ int f$8;
    private final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ -$$Lambda$MessagesStorage$Rsj_LL8Uz4Bh77DLk8Q7Jiw37h4(MessagesStorage messagesStorage, messages_Dialogs messages_dialogs, int i, int i2, int i3, int i4, int i5, Message message, int i6, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesStorage;
        this.f$1 = messages_dialogs;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = i3;
        this.f$5 = i4;
        this.f$6 = i5;
        this.f$7 = message;
        this.f$8 = i6;
        this.f$9 = longSparseArray;
        this.f$10 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$resetDialogs$52$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
