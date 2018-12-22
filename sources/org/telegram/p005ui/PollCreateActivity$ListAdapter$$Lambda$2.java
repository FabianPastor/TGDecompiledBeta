package org.telegram.p005ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.p005ui.Cells.PollEditTextCell;
import org.telegram.p005ui.PollCreateActivity.ListAdapter;

/* renamed from: org.telegram.ui.PollCreateActivity$ListAdapter$$Lambda$2 */
final /* synthetic */ class PollCreateActivity$ListAdapter$$Lambda$2 implements OnKeyListener {
    private final PollEditTextCell arg$1;

    PollCreateActivity$ListAdapter$$Lambda$2(PollEditTextCell pollEditTextCell) {
        this.arg$1 = pollEditTextCell;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return ListAdapter.lambda$onCreateViewHolder$2$PollCreateActivity$ListAdapter(this.arg$1, view, i, keyEvent);
    }
}
