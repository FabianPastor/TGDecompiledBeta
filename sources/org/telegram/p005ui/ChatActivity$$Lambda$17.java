package org.telegram.p005ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.ChatActivity$$Lambda$17 */
final /* synthetic */ class ChatActivity$$Lambda$17 implements OnTouchListener {
    private final ChatActivity arg$1;

    ChatActivity$$Lambda$17(ChatActivity chatActivity) {
        this.arg$1 = chatActivity;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$20$ChatActivity(view, motionEvent);
    }
}
