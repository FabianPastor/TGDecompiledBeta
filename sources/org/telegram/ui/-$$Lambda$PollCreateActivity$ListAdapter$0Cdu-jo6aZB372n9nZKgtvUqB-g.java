package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.Cells.PollEditTextCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollCreateActivity$ListAdapter$0Cdu-jo6aZB372n9nZKgtvUqB-g implements OnKeyListener {
    private final /* synthetic */ PollEditTextCell f$0;

    public /* synthetic */ -$$Lambda$PollCreateActivity$ListAdapter$0Cdu-jo6aZB372n9nZKgtvUqB-g(PollEditTextCell pollEditTextCell) {
        this.f$0 = pollEditTextCell;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return ListAdapter.lambda$onCreateViewHolder$2(this.f$0, view, i, keyEvent);
    }
}
