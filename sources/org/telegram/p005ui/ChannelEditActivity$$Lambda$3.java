package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ChannelEditActivity$$Lambda$3 */
final /* synthetic */ class ChannelEditActivity$$Lambda$3 implements OnEditorActionListener {
    private final ChannelEditActivity arg$1;

    ChannelEditActivity$$Lambda$3(ChannelEditActivity channelEditActivity) {
        this.arg$1 = channelEditActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$4$ChannelEditActivity(textView, i, keyEvent);
    }
}
