package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda181 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$messages_Dialogs f$1;
    public final /* synthetic */ HashSet f$10;
    public final /* synthetic */ TLRPC$messages_Dialogs f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ SparseArray f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ HashMap f$8;
    public final /* synthetic */ HashMap f$9;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda181(MessagesStorage messagesStorage, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, ArrayList arrayList4, HashMap hashMap, HashMap hashMap2, HashSet hashSet) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$messages_Dialogs;
        this.f$2 = tLRPC$messages_Dialogs2;
        this.f$3 = arrayList;
        this.f$4 = arrayList2;
        this.f$5 = arrayList3;
        this.f$6 = sparseArray;
        this.f$7 = arrayList4;
        this.f$8 = hashMap;
        this.f$9 = hashMap2;
        this.f$10 = hashSet;
    }

    public final void run() {
        this.f$0.lambda$processLoadedFilterPeers$45(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
