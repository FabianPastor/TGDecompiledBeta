package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ChannelEditInfoActivity$$Lambda$3 implements OnEditorActionListener {
    private final ChannelEditInfoActivity arg$1;

    ChannelEditInfoActivity$$Lambda$3(ChannelEditInfoActivity channelEditInfoActivity) {
        this.arg$1 = channelEditInfoActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$5$ChannelEditInfoActivity(textView, i, keyEvent);
    }
}
