package org.telegram.ui.Components;

import android.view.KeyEvent;
import android.view.View;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Components.ChatAttachAlertPollLayout;

public final /* synthetic */ class ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda1 implements View.OnKeyListener {
    public final /* synthetic */ PollEditTextCell f$0;

    public /* synthetic */ ChatAttachAlertPollLayout$ListAdapter$$ExternalSyntheticLambda1(PollEditTextCell pollEditTextCell) {
        this.f$0 = pollEditTextCell;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return ChatAttachAlertPollLayout.ListAdapter.lambda$onCreateViewHolder$2(this.f$0, view, i, keyEvent);
    }
}
