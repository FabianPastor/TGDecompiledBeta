package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.Cells.PollEditTextCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PollCreateActivity$ListAdapter$fHXG5XRT2mioX4wywMDd12jepYQ implements OnEditorActionListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ PollEditTextCell f$1;

    public /* synthetic */ -$$Lambda$PollCreateActivity$ListAdapter$fHXG5XRT2mioX4wywMDd12jepYQ(ListAdapter listAdapter, PollEditTextCell pollEditTextCell) {
        this.f$0 = listAdapter;
        this.f$1 = pollEditTextCell;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$onCreateViewHolder$1$PollCreateActivity$ListAdapter(this.f$1, textView, i, keyEvent);
    }
}
