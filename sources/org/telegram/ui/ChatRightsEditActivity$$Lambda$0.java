package org.telegram.ui;

import android.content.Context;
import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ChatRightsEditActivity$$Lambda$0 implements OnItemClickListener {
    private final ChatRightsEditActivity arg$1;
    private final Context arg$2;

    ChatRightsEditActivity$$Lambda$0(ChatRightsEditActivity chatRightsEditActivity, Context context) {
        this.arg$1 = chatRightsEditActivity;
        this.arg$2 = context;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$6$ChatRightsEditActivity(this.arg$2, view, i);
    }
}
