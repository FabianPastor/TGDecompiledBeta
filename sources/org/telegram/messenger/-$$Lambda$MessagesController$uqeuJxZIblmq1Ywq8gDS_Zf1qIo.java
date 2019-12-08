package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$uqeuJxZIblmq1Ywq8gDS_Zf1qIo implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ TL_messages_peerDialogs f$4;
    private final /* synthetic */ LongSparseArray f$5;
    private final /* synthetic */ TL_messages_dialogs f$6;

    public /* synthetic */ -$$Lambda$MessagesController$uqeuJxZIblmq1Ywq8gDS_Zf1qIo(MessagesController messagesController, int i, ArrayList arrayList, boolean z, TL_messages_peerDialogs tL_messages_peerDialogs, LongSparseArray longSparseArray, TL_messages_dialogs tL_messages_dialogs) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = z;
        this.f$4 = tL_messages_peerDialogs;
        this.f$5 = longSparseArray;
        this.f$6 = tL_messages_dialogs;
    }

    public final void run() {
        this.f$0.lambda$null$236$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
