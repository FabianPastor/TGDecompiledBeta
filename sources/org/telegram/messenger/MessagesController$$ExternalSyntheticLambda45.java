package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda45 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ int f$10;
    public final /* synthetic */ boolean f$11;
    public final /* synthetic */ int f$12;
    public final /* synthetic */ ArrayList f$13;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.messages_Dialogs f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ LongSparseArray f$7;
    public final /* synthetic */ LongSparseArray f$8;
    public final /* synthetic */ LongSparseArray f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda45(MessagesController messagesController, TLRPC.Message message, int i, TLRPC.messages_Dialogs messages_dialogs, ArrayList arrayList, boolean z, int i2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3, int i3, boolean z2, int i4, ArrayList arrayList2) {
        this.f$0 = messagesController;
        this.f$1 = message;
        this.f$2 = i;
        this.f$3 = messages_dialogs;
        this.f$4 = arrayList;
        this.f$5 = z;
        this.f$6 = i2;
        this.f$7 = longSparseArray;
        this.f$8 = longSparseArray2;
        this.f$9 = longSparseArray3;
        this.f$10 = i3;
        this.f$11 = z2;
        this.f$12 = i4;
        this.f$13 = arrayList2;
    }

    public final void run() {
        this.f$0.m313xcdbb21e4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13);
    }
}
