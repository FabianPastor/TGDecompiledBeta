package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class GroupCreateActivity$$Lambda$1 implements OnEditorActionListener {
    private final GroupCreateActivity arg$1;

    GroupCreateActivity$$Lambda$1(GroupCreateActivity groupCreateActivity) {
        this.arg$1 = groupCreateActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$GroupCreateActivity(textView, i, keyEvent);
    }
}