package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ChatActivity$$Lambda$55 implements OnClickListener {
    private final boolean[] arg$1;

    ChatActivity$$Lambda$55(boolean[] zArr) {
        this.arg$1 = zArr;
    }

    public void onClick(View view) {
        ChatActivity.lambda$processSelectedOption$69$ChatActivity(this.arg$1, view);
    }
}
