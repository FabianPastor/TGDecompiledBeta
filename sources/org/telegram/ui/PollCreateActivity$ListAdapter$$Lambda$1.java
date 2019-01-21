package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.Cells.PollEditTextCell;

final /* synthetic */ class PollCreateActivity$ListAdapter$$Lambda$1 implements OnEditorActionListener {
    private final ListAdapter arg$1;
    private final PollEditTextCell arg$2;

    PollCreateActivity$ListAdapter$$Lambda$1(ListAdapter listAdapter, PollEditTextCell pollEditTextCell) {
        this.arg$1 = listAdapter;
        this.arg$2 = pollEditTextCell;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$onCreateViewHolder$1$PollCreateActivity$ListAdapter(this.arg$2, textView, i, keyEvent);
    }
}
