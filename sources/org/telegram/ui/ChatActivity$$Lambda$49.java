package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$49 implements OnClickListener {
    private final boolean[] arg$1;

    ChatActivity$$Lambda$49(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        ChatActivity.lambda$createDeleteMessagesAlert$63$ChatActivity(this.arg$1, view);
    }
}
