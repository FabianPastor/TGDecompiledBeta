package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ChatEditActivity$$Lambda$3 implements OnEditorActionListener {
    private final ChatEditActivity arg$1;

    ChatEditActivity$$Lambda$3(ChatEditActivity chatEditActivity) {
        this.arg$1 = chatEditActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$4$ChatEditActivity(textView, i, keyEvent);
    }
}
