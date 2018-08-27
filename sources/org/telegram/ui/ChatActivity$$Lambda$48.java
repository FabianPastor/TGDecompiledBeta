package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$48 implements OnClickListener {
    private final boolean[] arg$1;

    ChatActivity$$Lambda$48(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        ChatActivity.lambda$createDeleteMessagesAlert$62$ChatActivity(this.arg$1, view);
    }
}
