package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ TLRPC$messages_Dialogs f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ LongSparseArray f$7;
    public final /* synthetic */ LongSparseArray f$8;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda41(MessagesController messagesController, int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, ArrayList arrayList4, LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = arrayList2;
        this.f$4 = arrayList3;
        this.f$5 = tLRPC$messages_Dialogs;
        this.f$6 = arrayList4;
        this.f$7 = longSparseArray;
        this.f$8 = longSparseArray2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedDialogFilters$8(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
