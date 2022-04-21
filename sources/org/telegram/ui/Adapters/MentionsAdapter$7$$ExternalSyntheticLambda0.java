package org.telegram.ui.Adapters;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Adapters.MentionsAdapter;

public final /* synthetic */ class MentionsAdapter$7$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MentionsAdapter.AnonymousClass7 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ LongSparseArray f$3;
    public final /* synthetic */ TLRPC.TL_error f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ MessagesController f$6;

    public /* synthetic */ MentionsAdapter$7$$ExternalSyntheticLambda0(MentionsAdapter.AnonymousClass7 r1, int i, ArrayList arrayList, LongSparseArray longSparseArray, TLRPC.TL_error tL_error, TLObject tLObject, MessagesController messagesController) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = longSparseArray;
        this.f$4 = tL_error;
        this.f$5 = tLObject;
        this.f$6 = messagesController;
    }

    public final void run() {
        this.f$0.m1345lambda$run$0$orgtelegramuiAdaptersMentionsAdapter$7(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
