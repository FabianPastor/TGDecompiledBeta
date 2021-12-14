package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;

public final /* synthetic */ class ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda2 implements TextView.OnEditorActionListener {
    public final /* synthetic */ ChatAttachAlertPollLayout.ListAdapter f$0;
    public final /* synthetic */ PollEditTextCell f$1;

    public /* synthetic */ ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda2(ChatAttachAlertPollLayout.ListAdapter listAdapter, PollEditTextCell pollEditTextCell) {
        this.f$0 = listAdapter;
        this.f$1 = pollEditTextCell;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$onCreateViewHolder$1(this.f$1, textView, i, keyEvent);
    }
}
