package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ChannelCreateActivity$$Lambda$3 implements OnEditorActionListener {
    private final ChannelCreateActivity arg$1;

    ChannelCreateActivity$$Lambda$3(ChannelCreateActivity channelCreateActivity) {
        this.arg$1 = channelCreateActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$5$ChannelCreateActivity(textView, i, keyEvent);
    }
}
