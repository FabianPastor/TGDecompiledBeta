package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$messages_Dialogs;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda293 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ HashMap f$10;
    public final /* synthetic */ HashMap f$11;
    public final /* synthetic */ HashSet f$12;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC$messages_Dialogs f$3;
    public final /* synthetic */ TLRPC$messages_Dialogs f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ TLObject f$6;
    public final /* synthetic */ ArrayList f$7;
    public final /* synthetic */ SparseArray f$8;
    public final /* synthetic */ ArrayList f$9;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda293(MessagesController messagesController, ArrayList arrayList, ArrayList arrayList2, TLRPC$messages_Dialogs tLRPC$messages_Dialogs, TLRPC$messages_Dialogs tLRPC$messages_Dialogs2, ArrayList arrayList3, TLObject tLObject, ArrayList arrayList4, SparseArray sparseArray, ArrayList arrayList5, HashMap hashMap, HashMap hashMap2, HashSet hashSet) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = tLRPC$messages_Dialogs;
        this.f$4 = tLRPC$messages_Dialogs2;
        this.f$5 = arrayList3;
        this.f$6 = tLObject;
        this.f$7 = arrayList4;
        this.f$8 = sparseArray;
        this.f$9 = arrayList5;
        this.f$10 = hashMap;
        this.f$11 = hashMap2;
        this.f$12 = hashSet;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$sendLoadPeersRequest$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, tLObject, tLRPC$TL_error);
    }
}
