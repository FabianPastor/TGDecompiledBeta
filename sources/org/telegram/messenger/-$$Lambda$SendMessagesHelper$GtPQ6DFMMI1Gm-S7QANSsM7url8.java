package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$GtPQ6DFMMI1Gm-S7QANSsM7url8 implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ LongSparseArray f$4;
    private final /* synthetic */ ArrayList f$5;
    private final /* synthetic */ ArrayList f$6;
    private final /* synthetic */ Peer f$7;
    private final /* synthetic */ TL_messages_forwardMessages f$8;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$GtPQ6DFMMI1Gm-S7QANSsM7url8(SendMessagesHelper sendMessagesHelper, long j, boolean z, boolean z2, LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, Peer peer, TL_messages_forwardMessages tL_messages_forwardMessages) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = z2;
        this.f$4 = longSparseArray;
        this.f$5 = arrayList;
        this.f$6 = arrayList2;
        this.f$7 = peer;
        this.f$8 = tL_messages_forwardMessages;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$sendMessage$9$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
    }
}