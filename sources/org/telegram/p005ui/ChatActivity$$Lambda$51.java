package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$51 */
final /* synthetic */ class ChatActivity$$Lambda$51 implements OnClickListener {
    private final boolean[] arg$1;

    ChatActivity$$Lambda$51(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        ChatActivity.lambda$createDeleteMessagesAlert$65$ChatActivity(this.arg$1, view);
    }
}
