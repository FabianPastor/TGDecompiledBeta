package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$100$$ExternalSyntheticLambda0 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatActivity.AnonymousClass100 f$0;
    public final /* synthetic */ MessageSeenView f$1;

    public /* synthetic */ ChatActivity$100$$ExternalSyntheticLambda0(ChatActivity.AnonymousClass100 r1, MessageSeenView messageSeenView) {
        this.f$0 = r1;
        this.f$1 = messageSeenView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$onClick$0(this.f$1, view, i);
    }
}
