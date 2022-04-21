package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda76 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_messages_forwardMessages f$10;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ LongSparseArray f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ MessageObject f$8;
    public final /* synthetic */ TLRPC.Peer f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda76(SendMessagesHelper sendMessagesHelper, long j, int i, boolean z, boolean z2, LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, MessageObject messageObject, TLRPC.Peer peer, TLRPC.TL_messages_forwardMessages tL_messages_forwardMessages) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = longSparseArray;
        this.f$6 = arrayList;
        this.f$7 = arrayList2;
        this.f$8 = messageObject;
        this.f$9 = peer;
        this.f$10 = tL_messages_forwardMessages;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m482lambda$sendMessage$14$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, tLObject, tL_error);
    }
}
