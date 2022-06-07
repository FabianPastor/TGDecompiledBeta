package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda126 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ HashMap f$10;
    public final /* synthetic */ HashSet f$11;
    public final /* synthetic */ HashMap f$12;
    public final /* synthetic */ HashMap f$13;
    public final /* synthetic */ TLRPC$messages_Dialogs f$2;
    public final /* synthetic */ TLRPC$messages_Dialogs f$3;
    public final /* synthetic */ ArrayList f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ ArrayList f$6;
    public final /* synthetic */ SparseArray f$7;
    public final /* synthetic */ ArrayList f$8;
    public final /* synthetic */ HashMap f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda126(MessagesController messagesController, HashMap hashMap, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, SparseArray sparseArray, ArrayList arrayList4, HashMap hashMap2, HashMap hashMap3, HashSet hashSet, HashMap hashMap4, HashMap hashMap5) {
        this.f$0 = messagesController;
        this.f$1 = hashMap;
        this.f$2 = tLRPC$messages_Dialogs;
        this.f$3 = tLRPC$messages_Dialogs2;
        this.f$4 = arrayList;
        this.f$5 = arrayList2;
        this.f$6 = arrayList3;
        this.f$7 = sparseArray;
        this.f$8 = arrayList4;
        this.f$9 = hashMap2;
        this.f$10 = hashMap3;
        this.f$11 = hashSet;
        this.f$12 = hashMap4;
        this.f$13 = hashMap5;
    }

    public final void run() {
        this.f$0.lambda$loadFilterPeers$12(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13);
    }
}
