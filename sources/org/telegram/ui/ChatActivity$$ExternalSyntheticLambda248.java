package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda248 implements RecyclerListView.OnItemClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageSeenView f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda248(ChatActivity chatActivity, MessageSeenView messageSeenView) {
        this.f$0 = chatActivity;
        this.f$1 = messageSeenView;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createMenu$158(this.f$1, view, i);
    }
}
