package org.telegram.ui.Components;

import android.view.View;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda53 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ TLRPC$ChatFull f$2;
    public final /* synthetic */ MessagesController f$3;
    public final /* synthetic */ RecyclerListView f$4;
    public final /* synthetic */ AtomicReference f$5;
    public final /* synthetic */ AtomicReference f$6;
    public final /* synthetic */ AtomicReference f$7;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda53(ChatActivityEnterView chatActivityEnterView, List list, TLRPC$ChatFull tLRPC$ChatFull, MessagesController messagesController, RecyclerListView recyclerListView, AtomicReference atomicReference, AtomicReference atomicReference2, AtomicReference atomicReference3) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = list;
        this.f$2 = tLRPC$ChatFull;
        this.f$3 = messagesController;
        this.f$4 = recyclerListView;
        this.f$5 = atomicReference;
        this.f$6 = atomicReference2;
        this.f$7 = atomicReference3;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$new$12(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, view, i);
    }
}
