package org.telegram.messenger;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda47 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ LongSparseArray f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda47(MessagesStorage messagesStorage, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        this.f$0 = messagesStorage;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = arrayList3;
        this.f$4 = longSparseArray;
    }

    public final void run() {
        this.f$0.m976lambda$readAllDialogs$38$orgtelegrammessengerMessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
