package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChatAttachAlert$$Lambda$0 implements OnItemClickListener {
    private final ChatAttachAlert arg$1;

    ChatAttachAlert$$Lambda$0(ChatAttachAlert chatAttachAlert) {
        this.arg$1 = chatAttachAlert;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$new$0$ChatAttachAlert(view, i);
    }
}
