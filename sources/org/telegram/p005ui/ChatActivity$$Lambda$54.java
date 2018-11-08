package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$54 */
final /* synthetic */ class ChatActivity$$Lambda$54 implements OnClickListener {
    private final boolean[] arg$1;

    ChatActivity$$Lambda$54(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        ChatActivity.lambda$processSelectedOption$70$ChatActivity(this.arg$1, view);
    }
}
